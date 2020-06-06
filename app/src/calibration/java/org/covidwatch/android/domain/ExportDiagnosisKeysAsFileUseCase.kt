package org.covidwatch.android.domain

import org.covidwatch.android.data.asDiagnosisKey
import org.covidwatch.android.data.asTemporaryExposureKey
import org.covidwatch.android.domain.ExportDiagnosisKeysAsFileUseCase.Params
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.exposurenotification.KeyFileWriter
import org.covidwatch.android.functional.Either
import java.io.File
import java.time.Duration
import java.time.Instant

class ExportDiagnosisKeysAsFileUseCase(
    private val enManager: ExposureNotificationManager,
    private val keyFileWriter: KeyFileWriter,
    dispatchers: AppCoroutineDispatchers
) : UseCase<List<File>, Params>(dispatchers) {

    override suspend fun run(params: Params?): Either<ENStatus, List<File>> {
        enManager.isEnabled().apply {
            success { enabled ->
                if (!enabled) return Either.Left(ENStatus.FailedServiceDisabled)
            }
            failure { return Either.Left(it) }
        }

        enManager.temporaryExposureKeyHistory().apply {
            success {
                val keys = it.mapIndexed { i, key ->
                    key.asDiagnosisKey()
                        .copy(
                            transmissionRisk = params?.riskLevels?.get(i)
                                ?: key.transmissionRiskLevel
                        ).asTemporaryExposureKey()
                }

                // TODO: 03.06.2020 Think of dynamic region
                val files = keyFileWriter.writeForKeys(
                    keys,
                    Instant.now().minus(Duration.ofDays(14)),
                    Instant.now(),
                    "US"
                )

                return Either.Right(files)
            }

            failure { return Either.Left(it) }
        }

        // It should not get here if the task was successful and known failures were handled
        return Either.Left(ENStatus.Failed)
    }

    data class Params(val riskLevels: List<Int>)
}