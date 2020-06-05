package org.covidwatch.android.domain

import android.content.ContentResolver
import android.net.Uri
import com.google.common.io.BaseEncoding
import org.covidwatch.android.data.diagnosiskeystoken.DiagnosisKeysToken
import org.covidwatch.android.data.diagnosiskeystoken.DiagnosisKeysTokenRepository
import org.covidwatch.android.domain.ProvideDiagnosisKeysFromFileUseCase.Params
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.functional.Either
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.security.SecureRandom

@Suppress("BlockingMethodInNonBlockingContext")
class ProvideDiagnosisKeysFromFileUseCase(
    private val enManager: ExposureNotificationManager,
    private val diagnosisKeysTokenRepository: DiagnosisKeysTokenRepository,
    private val contentResolver: ContentResolver,
    dispatchers: AppCoroutineDispatchers
) : UseCase<Unit, Params>(dispatchers) {
    private val base64 = BaseEncoding.base64()
    private val randomTokenByteLength = 32
    private val secureRandom: SecureRandom = SecureRandom()

    override suspend fun run(params: Params?): Either<ENStatus, Unit> {
        val uri = params?.uri ?: return Either.Left(ENStatus.Failed)
        Timber.d("Start providing a test diagnosis file: ${uri.path}")

        val file = File.createTempFile("test_report_", ".zip")
        val output = FileOutputStream(file)
        contentResolver.openInputStream(uri)?.copyTo(output)

        val files = listOf(file)
        val token = randomToken()

        enManager.provideDiagnosisKeys(files, token).apply {
            file.delete()
            success {
                Timber.d("Successfully provided a test diagnosis key: ${uri.path} with token: $token")
            }
            failure {
                Timber.d("Failed to provide a test diagnosis key: ${uri.path}")
                return Either.Left(it)
            }
        }

        diagnosisKeysTokenRepository.insert(DiagnosisKeysToken(token))
        return Either.Right(Unit)
    }

    data class Params(val uri: Uri)

    private fun randomToken(): String {
        val bytes = ByteArray(randomTokenByteLength)
        secureRandom.nextBytes(bytes)
        return base64.encode(bytes)
    }
}