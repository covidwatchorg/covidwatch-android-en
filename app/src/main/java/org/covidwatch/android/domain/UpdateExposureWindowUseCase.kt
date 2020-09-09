package org.covidwatch.android.domain

import org.covidwatch.android.data.EnConverter
import org.covidwatch.android.data.exposurewindow.ExposureWindowRepository
import org.covidwatch.android.data.model.asCovidExposureWindow
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.exposurenotification.Failure
import org.covidwatch.android.functional.Either
import timber.log.Timber

class UpdateExposureWindowUseCase(
    private val enManager: ExposureNotificationManager,
    private val exposureWindowRepository: ExposureWindowRepository,
    private val enConverter: EnConverter,
    dispatchers: AppCoroutineDispatchers
) : UseCase<Unit, Unit>(dispatchers) {
    override suspend fun run(params: Unit?): Either<Failure, Unit> {
        if (enManager.isDisabled()) return Either.Left(Failure.EnStatus.ServiceDisabled)

        Timber.d("Start ${javaClass.simpleName}")

        enManager.exposureWindows().apply {
            success { exposures ->
                val covidExposures = exposures.map { it.asCovidExposureWindow() }
                Timber.d("Exposure Information for: ${covidExposures.joinToString()}")

                exposureWindowRepository.saveExposures(covidExposures)
            }

            failure { status ->
                Timber.d("Failed to get Exposure Windows")
                return Either.Left(status)
            }
        }

        return Either.Right(Unit)
    }
}