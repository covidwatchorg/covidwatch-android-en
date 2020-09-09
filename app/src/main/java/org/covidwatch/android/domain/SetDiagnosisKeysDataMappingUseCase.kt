package org.covidwatch.android.domain

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import org.covidwatch.android.exposurenotification.Failure
import org.covidwatch.android.functional.Either
import org.covidwatch.android.work.SetDiagnosisKeysDataMappingWork
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

class SetDiagnosisKeysDataMappingUseCase(
    private val workManager: WorkManager,
    dispatchers: AppCoroutineDispatchers
) : UseCase<UUID, Unit>(dispatchers) {
    override suspend fun run(params: Unit?): Either<Failure, UUID> {
        Timber.d("Start ${javaClass.simpleName}.")

        val keysDataMappingWork: WorkRequest
        keysDataMappingWork = PeriodicWorkRequestBuilder<SetDiagnosisKeysDataMappingWork>(
            EXECUTION_PERIOD,
            TimeUnit.DAYS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            keysDataMappingWork
        )
        return Either.Right(keysDataMappingWork.id)
    }

    companion object {
        private const val WORK_NAME = "Set Diagnosis Keys Data Mapping"

        /**
         * Days between execution this use case. Exposure Notifications has a rate limit of 7 days
         * when it is allowed to set diagnosis keys data mapping.
         */
        private const val EXECUTION_PERIOD = 7L
    }
}