package org.covidwatch.android.domain

import com.google.common.io.BaseEncoding
import org.covidwatch.android.data.diagnosiskeystoken.DiagnosisKeysToken
import org.covidwatch.android.data.diagnosiskeystoken.DiagnosisKeysTokenRepository
import org.covidwatch.android.domain.ProvideDiagnosisKeysFromFileUseCase.Params
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.functional.Either
import java.io.File
import java.security.SecureRandom

class ProvideDiagnosisKeysFromFileUseCase(
    private val enManager: ExposureNotificationManager,
    private val diagnosisKeysTokenRepository: DiagnosisKeysTokenRepository,
    dispatchers: AppCoroutineDispatchers
) : UseCase<Unit, Params>(dispatchers) {
    private val base64 = BaseEncoding.base64()
    private val randomTokenByteLength = 32
    private val secureRandom: SecureRandom = SecureRandom()

    override suspend fun run(params: Params?): Either<ENStatus, Unit> {
        val keys = params?.files ?: emptyList()

        val token = randomToken()
        enManager.provideDiagnosisKeys(keys, token).apply {
            failure { return Either.Left(it) }
        }

        diagnosisKeysTokenRepository.insert(DiagnosisKeysToken(token))

        return Either.Right(Unit)
    }

    data class Params(val files: List<File>)

    private fun randomToken(): String {
        val bytes = ByteArray(randomTokenByteLength)
        secureRandom.nextBytes(bytes)
        return base64.encode(bytes)
    }
}