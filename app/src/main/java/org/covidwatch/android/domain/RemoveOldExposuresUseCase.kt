package org.covidwatch.android.domain

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import org.covidwatch.android.exposurenotification.Failure
import org.covidwatch.android.functional.Either
import org.covidwatch.android.work.RemoveOldExposuresWork
import timber.log.Timber
import java.util.concurrent.TimeUnit

class RemoveOldExposuresUseCase(
    private val workManager: WorkManager,
    dispatchers: AppCoroutineDispatchers
) : UseCase<Unit, Unit>(dispatchers) {
    override suspend fun run(params: Unit?): Either<Failure, Unit> {
        Timber.d("Start ${javaClass.simpleName}")

        val removeOldExposuresWork = OneTimeWorkRequestBuilder<RemoveOldExposuresWork>()
            .setInitialDelay(DAYS_TO_KEEP_EXPOSURES, TimeUnit.DAYS)
            .build()

        workManager.enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.KEEP,
            removeOldExposuresWork
        )
        return Either.Right(Unit)
    }

    companion object {
        private const val WORK_NAME = "Remove Old Exposures"
        const val DAYS_TO_KEEP_EXPOSURES = 30L
    }
}