package org.covidwatch.android.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.covidwatch.android.data.exposureinformation.ExposureInformationRepository
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
import org.covidwatch.android.domain.RemoveOldDataUseCase.Companion.DAYS_TO_KEEP_DATA
import org.koin.java.KoinJavaComponent.inject
import java.time.Instant
import java.time.temporal.ChronoUnit

class RemoveOldDataWork(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    private val exposuresRepository by inject(ExposureInformationRepository::class.java)
    private val diagnosesRepository by inject(PositiveDiagnosisRepository::class.java)

    override suspend fun doWork(): Result {
        val monthAgo = Instant.now().minus(DAYS_TO_KEEP_DATA, ChronoUnit.DAYS)

        exposuresRepository.deleteOlderThan(monthAgo)
        diagnosesRepository.deleteOlderThan(monthAgo)

        return Result.success()
    }
}