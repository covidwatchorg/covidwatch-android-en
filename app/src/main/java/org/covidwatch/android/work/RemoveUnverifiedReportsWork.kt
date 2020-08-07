package org.covidwatch.android.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber

class RemoveUnverifiedReportsWork(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    private val diagnosesRepository by inject(PositiveDiagnosisRepository::class.java)

    override suspend fun doWork(): Result {
        Timber.d("Start ${javaClass.simpleName}")

        diagnosesRepository.deleteCachedForUpload()
        return Result.success()
    }
}