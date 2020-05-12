package org.covidwatch.android.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber
import java.util.*

class ProvideDiagnosisKeysWork(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val exposureNotification by inject(ExposureNotificationManager::class.java)
    private val diagnosisRepository by inject(PositiveDiagnosisRepository::class.java)

    override suspend fun doWork(): Result {
        val diagnosisKeys = diagnosisRepository.diagnosisKeys(Date())
        Timber.d("Adding ${diagnosisKeys.size} positive diagnoses to exposure notification framework")
        val result = exposureNotification.provideDiagnosisKeys(diagnosisKeys)

        return result.left?.let { failure(it) } ?: Result.success()
    }

    private fun failure(status: Int?) =
        Result.failure(Data.Builder().putInt(FAILURE, status ?: UNKNOWN_FAILURE).build())

    companion object {
        const val FAILURE = "status"
        const val UNKNOWN_FAILURE = -1
    }
}