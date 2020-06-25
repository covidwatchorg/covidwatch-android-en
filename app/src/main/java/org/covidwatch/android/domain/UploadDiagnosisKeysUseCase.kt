package org.covidwatch.android.domain

import com.google.common.io.BaseEncoding
import org.covidwatch.android.data.*
import org.covidwatch.android.data.countrycode.CountryCodeRepository
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
import org.covidwatch.android.domain.UploadDiagnosisKeysUseCase.Params
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.functional.Either
import timber.log.Timber
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
) : UseCase<Unit, Params>(dispatchers) {

    //TODO: Where do we get the verification code from?
    private val defaultVerificationCode = "POSITIVE_TEST_123456"
    private val paddingSizeMin = 1024
    private val paddingSizeMax = 2048
    private val platform = "android"

    override suspend fun run(params: Params?): Either<ENStatus, Unit> {
        params ?: return Either.Left(ENStatus.Failed)

        /** TODO: 24.06.2020 Do something with [Params.report] */

        enManager.isEnabled().apply {
            success { enabled ->
                if (!enabled) {
                    Timber.d("Can't start ${javaClass.simpleName}. EN is not enabled")
                    return Either.Left(ENStatus.FailedServiceDisabled)
                }
            }
            failure {
                Timber.d("Can't start ${javaClass.simpleName}. EN is not enabled")
                return Either.Left(it)
            }
        }
        Timber.d("Start ${javaClass.simpleName}")
        enManager.temporaryExposureKeyHistory().apply {
            success {
                try {
                    val diagnosisKeys = it.mapIndexed { i, key ->
                        key.asDiagnosisKey().copy(transmissionRisk = params.riskLevels[i])
                    }
                    Timber.d("Diagnosis Keys ${diagnosisKeys.joinToString()}")

                    val regions = countryCodeRepository.exposureRelevantCountryCodes()
                    val uploadEndpoints = uriManager.uploadUris(regions)

                    Timber.d("Start Device Attestation")
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

                    Timber.d("Upload positive diagnosis: $positiveDiagnosis")
                    uploadEndpoints.forEach { url ->
                        diagnosisRepository.uploadDiagnosisKeys(url, positiveDiagnosis)
                    }

                    diagnosisRepository.addPositiveDiagnosisReport(params.report.copy(verified = true))
                    Timber.d("Uploaded positive diagnosis")
                    return Either.Right(Unit)
                } catch (e: Exception) {
                    Timber.d("Failed to upload positive diagnosis")
                    return Either.Left(ENStatus(e))
                }
            }

            failure {
                Timber.d("Failed to retrieve TEKs")
                return Either.Left(it)
            }
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

    data class Params(
        val riskLevels: List<Int>,
        val report: PositiveDiagnosisReport
    )
}