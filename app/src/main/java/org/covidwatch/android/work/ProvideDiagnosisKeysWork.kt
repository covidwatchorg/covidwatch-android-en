package org.covidwatch.android.work

import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.google.common.io.BaseEncoding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.covidwatch.android.R
import org.covidwatch.android.data.asExposureConfiguration
import org.covidwatch.android.data.diagnosiskeystoken.DiagnosisKeysToken
import org.covidwatch.android.data.diagnosiskeystoken.DiagnosisKeysTokenRepository
import org.covidwatch.android.data.keyfile.KeyFile
import org.covidwatch.android.data.keyfile.KeyFileRepository
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
import org.covidwatch.android.data.pref.PreferenceStorage
import org.covidwatch.android.domain.UpdateRegionsUseCase
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.exposurenotification.Failure
import org.covidwatch.android.extension.failure
import org.covidwatch.android.ui.Intents
import org.covidwatch.android.ui.MainActivity
import org.covidwatch.android.ui.Notifications
import org.covidwatch.android.ui.Notifications.Companion.DOWNLOAD_REPORTS_NOTIFICATION_ID
import org.covidwatch.android.ui.Urls
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber
import java.security.SecureRandom

class ProvideDiagnosisKeysWork(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val enManager by inject(ExposureNotificationManager::class.java)
    private val diagnosisRepository by inject(PositiveDiagnosisRepository::class.java)
    private val diagnosisKeysTokenRepository by inject(DiagnosisKeysTokenRepository::class.java)
    private val updateRegionsUseCase by inject(UpdateRegionsUseCase::class.java)
    private val notifications by inject(Notifications::class.java)
    private val preferences by inject(PreferenceStorage::class.java)
    private val keyFileRepository by inject(KeyFileRepository::class.java)

    private val base64 = BaseEncoding.base64()
    private val randomTokenByteLength = 32
    private val retries = 2
    private val secureRandom: SecureRandom = SecureRandom()

    private fun randomToken(): String {
        val bytes = ByteArray(randomTokenByteLength)
        secureRandom.nextBytes(bytes)
        return base64.encode(bytes)
    }

    override suspend fun doWork(): Result {
        runAttemptCount
        setForeground(
            ForegroundInfo(
                DOWNLOAD_REPORTS_NOTIFICATION_ID,
                notifications.downloadingReportsNotification()
            )
        )
        return withContext(Dispatchers.IO) {
            try {
                if (enManager.isDisabled()) {
                    notifications.downloadingReportsFailure(
                        R.string.notification_en_not_enabled,
                        Intent(context, MainActivity::class.java)
                    )
                    return@withContext failure(Failure.EnStatus.ServiceDisabled)
                }
                // Update regions data before we proceed because we need the latest exposure configuration

                // If it fails we use default values
                updateRegionsUseCase()

                val diagnosisKeys = diagnosisRepository.diagnosisKeys()
                Timber.d("Adding ${diagnosisKeys.size} batches of diagnoses to EN framework")

                val token = randomToken()
                val covidExposureConfiguration = preferences.exposureConfiguration
                val exposureConfiguration = covidExposureConfiguration.asExposureConfiguration()

                // Return success if no keys to provide
                if (diagnosisKeys.all { it.keys.isEmpty() }) return@withContext Result.success()

                diagnosisKeys.filter { it.keys.isNotEmpty() }.forEach { fileBatch ->
                    val keys = fileBatch.keys

                    enManager.provideDiagnosisKeys(keys, token, exposureConfiguration).apply {
                        success {
                            Timber.d("Added keys to EN with token: $token")
                            val dir = keys[0].parentFile
                            keys.forEachIndexed { i, file ->
                                keyFileRepository.add(
                                    KeyFile(
                                        fileBatch.region,
                                        fileBatch.batch,
                                        file,
                                        fileBatch.urls[i]
                                    )
                                )
                                file.delete()
                            }
                            dir?.delete()
                        }

                        failure {
                            val dir = keys[0].parentFile
                            keys.forEach { file -> file.delete() }
                            dir?.delete()
                            Timber.d("Failed to added keys to EN")
                            return@withContext failure(it)
                        }
                    }
                }

                diagnosisKeysTokenRepository.insert(
                    DiagnosisKeysToken(token, covidExposureConfiguration)
                )

                Result.success()
            } catch (e: Exception) {
                Timber.e(e)
                val failure = Failure(e)
                when (failure) {
                    Failure.EnStatus.Failed -> notifications.downloadingReportsFailure(
                        R.string.notification_general_problem,
                        Intents.browser(Urls.SUPPORT)
                    )
                    Failure.EnStatus.NotSupported -> notifications.downloadingReportsEnNotAvailable()
                    Failure.EnStatus.ServiceDisabled -> notifications.downloadingReportsFailure(
                        R.string.notification_en_not_enabled,
                        Intent(context, MainActivity::class.java)
                    )
                    Failure.EnStatus.Unauthorized -> notifications.downloadingReportsFailure(
                        R.string.notification_app_unauthorized,
                        Intents.browser(Urls.SUPPORT)
                    )
                    Failure.NetworkError -> notifications.downloadingReportsNetworkFailure()
                    // Retry 2 times and then show a error to users
                    is Failure.ServerError -> if (runAttemptCount < retries) return@withContext Result.retry() else notifications.downloadingReportsFailure(
                        context.getString(R.string.notification_server_problem, failure.error),
                        Intents.browser(Urls.SUPPORT)
                    )
                }
                failure(failure)
            }
        }
    }
}