package org.covidwatch.android.domain

import org.covidwatch.android.data.diagnosiskeystoken.DiagnosisKeysTokenRepository
import org.covidwatch.android.data.exposureinformation.ExposureInformationRepository
import org.covidwatch.android.data.toCovidExposureInformation
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.functional.Either

class UpdateExposureInformationUseCase(
    private val exposureNotificationManager: ExposureNotificationManager,
    private val tokenRepository: DiagnosisKeysTokenRepository,
    private val exposureInformationRepository: ExposureInformationRepository,
    dispatchers: AppCoroutineDispatchers
) : UseCase<Unit, Unit>(dispatchers) {
    override suspend fun run(params: Unit?): Either<ENStatus, Unit> {
        tokenRepository.exposedTokens().forEach { keysToken ->
            exposureNotificationManager.getExposureInformation(keysToken.token).apply {
                success { information ->

                    val exposureInformation = information.map { remoteInformation ->
                        remoteInformation.toCovidExposureInformation()
                    }

                    exposureInformationRepository.saveExposureInformation(exposureInformation)
                    tokenRepository.delete(keysToken)
                }

                failure { status ->
                    return Either.Left(status)
                }
            }
        }

        return Either.Right(Unit)
    }
}