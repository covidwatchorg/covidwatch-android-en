package org.covidwatch.android.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.common.io.BaseEncoding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.covidwatch.android.data.diagnosiskeystoken.DiagnosisKeysToken
import org.covidwatch.android.data.diagnosiskeystoken.DiagnosisKeysTokenRepository
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber
import java.security.SecureRandom

class ProvideDiagnosisKeysWork(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val exposureNotification by inject(ExposureNotificationManager::class.java)
    private val diagnosisRepository by inject(PositiveDiagnosisRepository::class.java)
    private val diagnosisKeysTokenRepository by inject(DiagnosisKeysTokenRepository::class.java)

    private val base64 = BaseEncoding.base64()
    private val randomTokenByteLength = 32
    private val secureRandom: SecureRandom = SecureRandom()

    private fun randomToken(): String {
        val bytes = ByteArray(randomTokenByteLength)
        secureRandom.nextBytes(bytes)
        return base64.encode(bytes)
    }

    override suspend fun doWork(): Result {
        withContext(Dispatchers.IO) {
            val diagnosisKeys = diagnosisRepository.diagnosisKeys()
            Timber.d("Adding ${diagnosisKeys.size} positive diagnoses to exposure notification framework")

            val token = randomToken()
            diagnosisKeys.forEach {
                val keys = it.keys
                exposureNotification.provideDiagnosisKeys(keys, token).apply {
                    success {
                        //TODO: Delete empty folder
                        keys.forEach { file -> file.delete() }
                    }
                    //TODO: Handle failed files
                }
            }

            diagnosisKeysTokenRepository.insert(DiagnosisKeysToken(token))
        }
        return Result.success()
    }
}