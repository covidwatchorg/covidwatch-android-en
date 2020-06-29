package org.covidwatch.android.data

import com.google.common.base.Joiner
import com.google.common.io.BaseEncoding
import org.covidwatch.android.data.diagnosisverification.DiagnosisVerificationRepository
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

class DiagnosisVerificationManager(
    private val verificationRepository: DiagnosisVerificationRepository
) {
    // NOTE: The server expects base64 with padding.
    private val encoding = BaseEncoding.base64()
    private val commaJoiner = Joiner.on(',')

    private fun sha256(text: String): ByteArray {
        val sha256Digest = MessageDigest.getInstance("SHA-256")
        val textBytes = text.toByteArray(StandardCharsets.UTF_8)
        sha256Digest.update(textBytes)
        return sha256Digest.digest()
    }

    suspend fun verify(
        keys: List<DiagnosisKey>,
        verificationCode: String
    ): String? {
        val token = verificationRepository.verify(verificationCode)
        val hmac = encoding.encode(sha256(keys(keys)))
        return verificationRepository.certificate(token, hmac)
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