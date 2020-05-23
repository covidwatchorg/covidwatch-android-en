package org.covidwatch.android.domain

import com.google.common.io.BaseEncoding
import org.covidwatch.android.BuildConfig
import org.covidwatch.android.data.PositiveDiagnosis
import org.covidwatch.android.data.SafetyNetManager
import org.covidwatch.android.data.UriManager
import org.covidwatch.android.data.asDiagnosisKey
import org.covidwatch.android.data.countrycode.CountryCodeRepository
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.functional.Either
import java.security.SecureRandom

class UploadDiagnosisKeysUseCase(
    private val enManager: ExposureNotificationManager,
    private val diagnosisRepository: PositiveDiagnosisRepository,
    private val countryCodeRepository: CountryCodeRepository,
    private val safetyNetManager: SafetyNetManager,
    private val uriManager: UriManager,
    private val appPackageName: String,
    private val random: SecureRandom,
    private val encoding: BaseEncoding,
    dispatchers: AppCoroutineDispatchers
) : UseCase<Unit, Unit>(dispatchers) {

    //TODO: Where do we get the verification code from?
    private val defaultVerificationCode = "POSITIVE_TEST_123456"
    private val paddingSizeMin = 1024
    private val paddingSizeMax = 2048
    private val platform = "android"

    override suspend fun run(params: Unit?): Either<ENStatus, Unit> {
        enManager.temporaryExposureKeyHistory().apply {
            success {
                val diagnosisKeys = it.map { key -> key.asDiagnosisKey() }

                val regions = countryCodeRepository.exposureRelevantCountryCodes()
                val uploadEndpoints = uriManager.uploadUris(regions)

                val attestation = safetyNetManager.attestFor(
                    diagnosisKeys,
                    regions,
                    defaultVerificationCode
                ) ?: return Either.Left(ENStatus.FailedInternal)

                val positiveDiagnosis = PositiveDiagnosis(
                    diagnosisKeys,
                    regions,
                    appPackageName,
                    platform,
                    defaultVerificationCode,
                    attestation,
                    randomPadding()
                )

                uploadEndpoints.forEach {
                    diagnosisRepository.uploadDiagnosisKeys(it, positiveDiagnosis)
                }
                return Either.Right(Unit)
            }

            failure { return Either.Left(it) }
        }
        return Either.Right(Unit)
    }

    private fun randomPadding(): String {
        val range = paddingSizeMax - paddingSizeMin
        val paddingLen = paddingSizeMin + random.nextInt(range)

        // Approximate the base64 blowup.
        val numBytes = (paddingLen.toDouble() * 0.75).toInt()
        val bytes = ByteArray(numBytes)
        random.nextBytes(bytes)

        return encoding.encode(bytes)
    }
}