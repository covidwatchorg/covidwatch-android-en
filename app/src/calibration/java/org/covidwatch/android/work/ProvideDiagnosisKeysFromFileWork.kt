package org.covidwatch.android.work

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.work.*
import com.google.android.gms.nearby.exposurenotification.ExposureConfiguration
import com.google.common.io.BaseEncoding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.covidwatch.android.attenuationDurationThresholds
import org.covidwatch.android.data.diagnosiskeystoken.DiagnosisKeysToken
import org.covidwatch.android.data.diagnosiskeystoken.DiagnosisKeysTokenRepository
import org.covidwatch.android.data.model.asCovidExposureConfiguration
import org.covidwatch.android.data.pref.PreferenceStorage
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.exposurenotification.Failure
import org.covidwatch.android.extension.failure
import org.covidwatch.android.ui.Notifications
import org.covidwatch.android.ui.Notifications.Companion.DOWNLOAD_REPORTS_NOTIFICATION_ID
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.security.SecureRandom

class ProvideDiagnosisKeysFromFileWork(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val enManager by inject(ExposureNotificationManager::class.java)
    private val diagnosisKeysTokenRepository by inject(DiagnosisKeysTokenRepository::class.java)
    private val notifications by inject(Notifications::class.java)
    private val preferences by inject(PreferenceStorage::class.java)
    private val contentResolver by inject(ContentResolver::class.java)
    private val base64 = BaseEncoding.base64()
    private val randomTokenByteLength = 32
    private val secureRandom: SecureRandom = SecureRandom()

    private fun randomToken(): String {
        val bytes = ByteArray(randomTokenByteLength)
        secureRandom.nextBytes(bytes)
        return base64.encode(bytes)
    }

    override suspend fun doWork() = withContext(Dispatchers.IO) {
        setForeground(
            ForegroundInfo(
                DOWNLOAD_REPORTS_NOTIFICATION_ID,
                notifications.downloadingReportsNotification()
            )
        )

        try {
            val uriString =
                inputData.getString(URI) ?: return@withContext failure(Failure.EnStatus.Failed)
            val uri = Uri.parse(uriString)
            Timber.d("Start providing a test diagnosis file: ${uri.path}")

            val file = File.createTempFile("test_report_", ".zip")
            val output = FileOutputStream(file)
            contentResolver.openInputStream(uri)?.copyTo(output)

            val files = listOf(file)

            val configuration = preferences.exposureConfiguration
            val configurationBuilder = ExposureConfiguration.ExposureConfigurationBuilder()
                .setMinimumRiskScore(configuration.minimumRiskScore)
                .setAttenuationScores(*configuration.attenuationScores)
                .setDaysSinceLastExposureScores(*configuration.daysSinceLastExposureScores)
                .setDurationScores(*configuration.durationScores)
                .setTransmissionRiskScores(*configuration.transmissionRiskScores)
                .setAttenuationWeight(configuration.attenuationWeight ?: 0)
                .setAttenuationWeight(configuration.attenuationWeight ?: 0)
                .setDaysSinceLastExposureWeight(configuration.daysSinceLastExposureWeight ?: 0)
                .setDurationWeight(configuration.durationWeight ?: 0)
                .setTransmissionRiskWeight(configuration.transmissionRiskWeight ?: 0)

            for (thresholds in attenuationDurationThresholds) {
                val exposureConfiguration = configurationBuilder
                    .setDurationAtAttenuationThresholds(*thresholds)
                    .build()

                val token = randomToken()
                enManager.provideDiagnosisKeys(
                    files,
                    token,
                    exposureConfiguration
                ).apply {
                    success {
                        Timber.d("Successfully provided a test diagnosis key: ${uri.path} with token: $token")
                    }
                    failure {
                        Timber.d("Failed to provide a test diagnosis key: ${uri.path}")
                        return@withContext failure(it)
                    }
                }

                diagnosisKeysTokenRepository.insert(
                    DiagnosisKeysToken(
                        token,
                        exposureConfiguration = exposureConfiguration.asCovidExposureConfiguration()
                    )
                )
            }

            file.delete()

            Result.success()
        } catch (e: Exception) {
            Timber.e(e)
            failure(Failure(e))
        }
    }

    companion object {
        private const val URI = "uri"
        private const val WORK_NAME = "Provide Diagnosis Keys From File"

        fun start(uri: Uri, workManager: WorkManager) {
            val work = OneTimeWorkRequestBuilder<ProvideDiagnosisKeysFromFileWork>()
                .setInputData(
                    Data.Builder().putString(URI, uri.toString())
                        .build()
                )
                .build()

            workManager.enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                work
            )
        }
    }
}