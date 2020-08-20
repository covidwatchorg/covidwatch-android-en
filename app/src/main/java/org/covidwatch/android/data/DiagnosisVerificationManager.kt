package org.covidwatch.android.data

import com.google.common.base.Joiner
import com.google.common.io.BaseEncoding
import org.covidwatch.android.data.diagnosisverification.DiagnosisVerificationRepository
import org.covidwatch.android.exposurenotification.Failure
import org.covidwatch.android.exposurenotification.NoConnectionException
import org.covidwatch.android.exposurenotification.ServerException
import org.covidwatch.android.functional.Either
import org.covidwatch.android.ui.util.DateFormatter
import java.nio.charset.StandardCharsets
import java.time.Instant
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKey

// TODO: 02.08.2020 Remove this extra level of abstraction
//  and move the logic into DiagnosisVerificationRepository
class DiagnosisVerificationManager(
    private val verificationRepository: DiagnosisVerificationRepository
) {
    private val encoding = BaseEncoding.base64()
    private val commaJoiner = Joiner.on(',')

    private fun sha256(text: String, skey: SecretKey): ByteArray {
        val mac = Mac.getInstance("HMACSHA256")
        mac.init(skey)

        val textBytes = text.toByteArray(StandardCharsets.UTF_8)
        mac.update(textBytes)
        return mac.doFinal()
    }

    suspend fun verify(code: String): Either<Failure, Verification> {
        try {
            val verifyCodeResponse = verificationRepository.verify(code)

            val symptomDate = verifyCodeResponse.symptomDate
                ?: throw ServerException("Verification Test Date is null")

            return Either.Right(
                Verification(
                    verifyCodeResponse.token ?: throw ServerException("Verification Token is null"),
                    DateFormatter.symptomDate(symptomDate),
                    verifyCodeResponse.testType
                        ?: throw ServerException("Verification Test Type is null")
                )
            )
        } catch (e: Exception) {
            return when {
                e is NoConnectionException -> {
                    Either.Left(Failure.NetworkError)
                }
                e.message?.contains("verification code used") == true -> {
                    Either.Left(Failure.VerificationCodeUsed)
                }
                e.message?.contains("expired") == true -> {
                    Either.Left(Failure.VerificationCodeExpired)
                }
                else -> {
                    Either.Left(Failure.CodeVerification(e.message))
                }
            }
        }
    }

    suspend fun certificate(
        token: String,
        keys: List<DiagnosisKey>
    ): Certificate {
        val kgen: KeyGenerator = KeyGenerator.getInstance("HMACSHA256")
        val skey: SecretKey = kgen.generateKey()

        val hmac = encoding.encode(sha256(keys(keys), skey))
        val certificate = verificationRepository.certificate(token, hmac)
        return Certificate(certificate, skey.encoded)
    }

    private fun keys(
        keys: List<DiagnosisKey>
    ): String {
        val keysBase64 = keys
            .sortedBy { encoding.encode(it.key) }
            .map {
                "${encoding.encode(it.key)}.${it.rollingStartNumber}.${it.rollingPeriod}.${it.transmissionRisk}"
            }

        return commaJoiner.join(keysBase64)
    }
}

class Certificate(
    val certificate: String,
    val hmacKey: ByteArray
)

data class Verification(
    val token: String,
    val symptomDate: Instant?,
    val testType: String
)