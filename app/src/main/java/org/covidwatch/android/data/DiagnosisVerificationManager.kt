package org.covidwatch.android.data

import com.google.common.base.Joiner
import com.google.common.io.BaseEncoding
import org.covidwatch.android.data.diagnosisverification.DiagnosisVerificationRepository
import org.covidwatch.android.exposurenotification.ServerException
import java.nio.charset.StandardCharsets
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKey

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

    suspend fun verify(
        keys: List<DiagnosisKey>,
        verificationCode: String
    ): CodeVerification {
        val verifyCodeResponse = verificationRepository.verify(verificationCode)
        val token = verifyCodeResponse.token ?: throw ServerException("Verification Token is null")
        val testType =
            verifyCodeResponse.testType ?: throw ServerException("Verification Test Type is null")
        val symptomDate =
            verifyCodeResponse.symptomDate
                ?: throw ServerException("Verification Test Date is null")

        val kgen: KeyGenerator = KeyGenerator.getInstance("HMACSHA256")
        val skey: SecretKey = kgen.generateKey()

        val hmac = encoding.encode(sha256(keys(keys), skey))
        val certificate = verificationRepository.certificate(token, hmac)
        return CodeVerification(testType, symptomDate, token, certificate, skey.encoded)
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

data class CodeVerification(
    val testType: String,
    val symptomDate: String,
    val token: String,
    val certificate: String,
    val hmacKey: ByteArray
)