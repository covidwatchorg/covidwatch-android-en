package org.covidwatch.android.domain

import com.google.android.gms.nearby.exposurenotification.TemporaryExposureKey
import org.covidwatch.android.data.model.asDiagnosisKey
import org.covidwatch.android.data.model.asTemporaryExposureKey
import org.covidwatch.android.domain.ExportDiagnosisKeysAsFileUseCase.Params
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.exposurenotification.Failure
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

    override suspend fun run(params: Params?): Either<Failure, List<File>> {
        params ?: return Either.Left(Failure.EnStatus.Failed)

        enManager.isEnabled().apply {
            success { enabled ->
                if (!enabled) return Either.Left(Failure.EnStatus.ServiceDisabled)
            }
            failure { return Either.Left(it) }
        }

        val keys = params.keys.mapIndexed { i, key ->
            key.asDiagnosisKey().copy(
                transmissionRisk = params.riskLevels[i],
                rollingPeriod = 144
            ).asTemporaryExposureKey()
        }

        // TODO: 03.06.2020 Think of dynamic region
        val files = keyFileWriter.writeForKeys(
            keys,
            Instant.now().minus(Duration.ofDays(keys.size.toLong())),
            Instant.now(),
            "US"
        )

        return Either.Right(files)
    }

    data class Params(
        val riskLevels: List<Int>,
        val keys: MutableList<TemporaryExposureKey>
    )
}