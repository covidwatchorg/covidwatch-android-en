package org.covidwatch.android.data

import com.google.android.gms.safetynet.SafetyNetClient
import com.google.common.base.Joiner
import com.google.common.io.BaseEncoding
import org.covidwatch.android.extension.await
import timber.log.Timber
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Encapsulates getting an attestation of confidence that the device we are currently running on is
 * genuine, not compromised such that we should not trust it.
 *
 *
 * See https://developer.android.com/training/safetynet
 *
 *
 * In this example we compose the SafetyNet nonce from:
 *
 *
 *  * The package name of this app.
 *  * The diagnosis keys, sorted then comma separated, each in the format:
 *
 *  * base64(key_bytes) + "." + interval_number + "." + interval_count + "." + transmisionRisk(0-8)
 *
 *  * The comma separated sorted list of regions for which the keys are to be distributed, and
 *  * The verification code indicating positive diagnosis.
 *
 *
 *
 * All joined with the pipe ("|") character.
 */
class SafetyNetManager(
    private val apiKey: String,
    private val packageName: String,
    private val safetyNet: SafetyNetClient
) {
    // NOTE: The server expects base64 with padding.
    private val encoding = BaseEncoding.base64()
    private val commaJoiner = Joiner.on(',')
    private val pipeJoiner = Joiner.on('|')

    private fun sha256(text: String): ByteArray {
        return try {
            val sha256Digest =
                MessageDigest.getInstance("SHA-256")
            val textBytes =
                text.toByteArray(StandardCharsets.UTF_8)
            sha256Digest.update(textBytes)
            sha256Digest.digest()
        } catch (e: NoSuchAlgorithmException) {
            // TODO: Some better exception.
            throw RuntimeException(e)
        }
    }

    /**
     * Obtains from SafetyNet an attestation token using the given keys and regions, plus the app
     * package name as the nonce to sign.
     */
    suspend fun attestFor(
        keys: List<DiagnosisKey>,
        regions: List<String>,
        verificationCode: String
    ): String? {
        Timber.i("Getting SafetyNet attestation.")

        val cleartext = cleartextFor(keys, regions, verificationCode)
        val nonce = encoding.encode(sha256(cleartext))
        val attest = safetyNet.attest(nonce.toByteArray(), apiKey).await()

        return attest.right?.jwsResult
    }

    private fun cleartextFor(
        keys: List<DiagnosisKey>,
        regions: List<String>,
        verificationCode: String
    ): String {
        val parts = mutableListOf<String>()
        // Order of the parts is important here. Don't shuffle them, or the server may not be able to
        // verify the attestation.
        parts.add(packageName)
        parts.add(keys(keys))
        parts.add(regions(regions))
        parts.add(verificationCode)
        return pipeJoiner.join(parts)
    }

    private fun keys(
        keys: List<DiagnosisKey>
    ): String {
        val keysBase64 = keys
            .map {
                "${encoding.encode(it.key)}.${it.rollingStartNumber}.${it.rollingPeriod}.${it.transmissionRisk}"
            }
            .sorted()

        return commaJoiner.join(keysBase64)
    }

    private fun regions(regions: List<String>): String {
        return commaJoiner.join(regions.sorted())
    }
}