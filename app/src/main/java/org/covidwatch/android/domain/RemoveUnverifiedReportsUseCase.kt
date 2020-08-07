package org.covidwatch.android.domain

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import org.covidwatch.android.exposurenotification.Failure
import org.covidwatch.android.functional.Either
import org.covidwatch.android.work.RemoveUnverifiedReportsWork
import timber.log.Timber
import java.util.concurrent.TimeUnit

class RemoveUnverifiedReportsUseCase(
    private val workManager: WorkManager,
    dispatchers: AppCoroutineDispatchers
) : UseCase<Unit, Unit>(dispatchers) {
    override suspend fun run(params: Unit?): Either<Failure, Unit> {
        Timber.d("Start ${javaClass.simpleName}")

        val removeOldExposuresWork = OneTimeWorkRequestBuilder<RemoveUnverifiedReportsWork>()
            .setInitialDelay(HOURS_VERIFICATION_CODE_VALID, TimeUnit.HOURS)
            .build()

        workManager.enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            removeOldExposuresWork
        )
        return Either.Right(Unit)
    }

    companion object {
        private const val WORK_NAME = "Remove Unverified Reports"
        private const val HOURS_VERIFICATION_CODE_VALID = 1L
    }
}