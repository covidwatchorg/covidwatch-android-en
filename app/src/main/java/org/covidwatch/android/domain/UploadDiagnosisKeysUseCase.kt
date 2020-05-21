package org.covidwatch.android.domain

import org.covidwatch.android.data.PositiveDiagnosis
import org.covidwatch.android.data.asDiagnosisKey
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.functional.Either

class UploadDiagnosisKeysUseCase(
    private val enManager: ExposureNotificationManager,
    private val diagnosisRepository: PositiveDiagnosisRepository,
    dispatchers: AppCoroutineDispatchers
) : UseCase<Unit, Unit>(dispatchers) {
    override suspend fun run(params: Unit?): Either<ENStatus, Unit> {
        enManager.temporaryExposureKeyHistory().apply {
            success {
                val diagnosisKeys = it.map { key -> key.asDiagnosisKey() }
                val positiveDiagnosis =
                    PositiveDiagnosis(
                        diagnosisKeys,
                        "phaNumber"
                    )
                diagnosisRepository.uploadDiagnosisKeys(positiveDiagnosis)
                return Either.Right(Unit)
            }

            failure { return Either.Left(it) }
        }
        return Either.Right(Unit)
    }
}