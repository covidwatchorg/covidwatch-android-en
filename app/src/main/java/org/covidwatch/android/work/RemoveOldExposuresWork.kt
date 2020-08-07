package org.covidwatch.android.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.covidwatch.android.data.exposureinformation.ExposureInformationRepository
import org.covidwatch.android.domain.RemoveOldExposuresUseCase.Companion.DAYS_TO_KEEP_EXPOSURES
import org.koin.java.KoinJavaComponent.inject
import java.time.Instant
import java.time.temporal.ChronoUnit

class RemoveOldExposuresWork(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    private val exposuresRepository by inject(ExposureInformationRepository::class.java)

    override suspend fun doWork(): Result {
        exposuresRepository.deleteOlderThan(
            Instant.now().minus(DAYS_TO_KEEP_EXPOSURES, ChronoUnit.DAYS)
        )
        return Result.success()
    }
}