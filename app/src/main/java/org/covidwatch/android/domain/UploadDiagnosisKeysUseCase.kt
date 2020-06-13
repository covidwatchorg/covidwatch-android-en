package org.covidwatch.android.domain

import com.google.common.io.BaseEncoding
import org.covidwatch.android.data.*
import org.covidwatch.android.data.countrycode.CountryCodeRepository
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
import org.covidwatch.android.domain.UploadDiagnosisKeysUseCase.Params
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.functional.Either
import java.security.SecureRandom
import java.util.*

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
) : UseCase<Unit, Params>(dispatchers) {

    //TODO: Where do we get the verification code from?
    private val defaultVerificationCode = "POSITIVE_TEST_123456"
    private val paddingSizeMin = 1024
    private val paddingSizeMax = 2048
    private val platform = "android"

    override suspend fun run(params: Params?): Either<ENStatus, Unit> {
        enManager.isEnabled().apply {
            success { enabled ->
                if (!enabled) return Either.Left(ENStatus.FailedServiceDisabled)
            }
            failure { return Either.Left(it) }
        }
        enManager.temporaryExposureKeyHistory().apply {
            success {
                try {
                    val diagnosisKeys = it.mapIndexed { i, key ->
                        key.asDiagnosisKey()
                            .copy(
                                transmissionRisk = params?.riskLevels?.get(i)
                                    ?: key.transmissionRiskLevel
                            )
                    }

                    val regions = countryCodeRepository.exposureRelevantCountryCodes()
                    val uploadEndpoints = uriManager.uploadUris(regions)

                    val attestation = safetyNetManager.attestFor(
                        diagnosisKeys,
                        regions,
                        defaultVerificationCode
                    ) ?: return Either.Left(ENStatus.FailedDeviceAttestation)

                    val positiveDiagnosis = PositiveDiagnosis(
                        diagnosisKeys,
                        regions,
                        appPackageName,
                        platform,
                        defaultVerificationCode,
                        attestation,
                        randomPadding()
                    )

                    uploadEndpoints.forEach { url ->
                        diagnosisRepository.uploadDiagnosisKeys(url, positiveDiagnosis)
                    }

                    diagnosisRepository.addPositiveDiagnosisReport(
                        PositiveDiagnosisReport(
                            verified = true,
                            reportDate = Date()
                        )
                    )
                    return Either.Right(Unit)
                } catch (e: Exception) {
                    return Either.Left(ENStatus(e))
                }
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

    data class Params(val riskLevels: List<Int>)
}