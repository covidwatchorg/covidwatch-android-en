package org.covidwatch.android.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.google.common.io.BaseEncoding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.covidwatch.android.data.diagnosiskeystoken.DiagnosisKeysToken
import org.covidwatch.android.data.diagnosiskeystoken.DiagnosisKeysTokenRepository
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.extension.failure
import org.covidwatch.android.ui.Notifications
import org.covidwatch.android.ui.Notifications.Companion.DOWNLOAD_REPORTS_NOTIFICATION_ID
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber
import java.security.SecureRandom

class ProvideDiagnosisKeysWork(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val enManager by inject(ExposureNotificationManager::class.java)
    private val diagnosisRepository by inject(PositiveDiagnosisRepository::class.java)
    private val diagnosisKeysTokenRepository by inject(DiagnosisKeysTokenRepository::class.java)
    private val notifications by inject(Notifications::class.java)

    private val base64 = BaseEncoding.base64()
    private val randomTokenByteLength = 32
    private val secureRandom: SecureRandom = SecureRandom()

    private fun randomToken(): String {
        val bytes = ByteArray(randomTokenByteLength)
        secureRandom.nextBytes(bytes)
        return base64.encode(bytes)
    }

    override suspend fun doWork(): Result {
        setForeground(
            ForegroundInfo(
                DOWNLOAD_REPORTS_NOTIFICATION_ID,
                notifications.downloadingReportsNotification()
            )
        )
        return withContext(Dispatchers.IO) {
            try {
                val diagnosisKeys = diagnosisRepository.diagnosisKeys()
                Timber.d("Adding ${diagnosisKeys.size} positive diagnoses to exposure notification framework")

                val token = randomToken()
                diagnosisKeys.forEach {
                    val keys = it.keys
                    enManager.provideDiagnosisKeys(keys, token).apply {
                        success {
                            //TODO: Delete empty folder
                            keys.forEach { file -> file.delete() }
                        }
                        //TODO: Handle failed files
                    }
                }

                diagnosisKeysTokenRepository.insert(DiagnosisKeysToken(token))
                Result.success()
            } catch (e: Exception) {
                failure(ENStatus(e))
            }
        }
    }
}