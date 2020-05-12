package org.covidwatch.android.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.google.common.io.BaseEncoding
import org.covidwatch.android.data.diagnosiskeystoken.DiagnosisKeysToken
import org.covidwatch.android.data.diagnosiskeystoken.DiagnosisKeysTokenRepository
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber
import java.security.SecureRandom
import java.util.*

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
        val diagnosisKeys = diagnosisRepository.diagnosisKeys(Date())
        Timber.d("Adding ${diagnosisKeys.size} positive diagnoses to exposure notification framework")

        val token = randomToken()
        val result = exposureNotification.provideDiagnosisKeys(diagnosisKeys, token)
        result.left?.let { return failure(it) }

        diagnosisKeysTokenRepository.insert(DiagnosisKeysToken(token))

        return Result.success()
    }

    private fun failure(status: Int?) =
        Result.failure(Data.Builder().putInt(FAILURE, status ?: UNKNOWN_FAILURE).build())

    companion object {
        const val FAILURE = "status"
        const val UNKNOWN_FAILURE = -1
    }
}