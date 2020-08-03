package org.covidwatch.android.domain

import com.google.android.gms.nearby.exposurenotification.TemporaryExposureKey
import com.google.common.io.BaseEncoding
import org.covidwatch.android.data.*
import org.covidwatch.android.data.countrycode.CountryCodeRepository
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
import org.covidwatch.android.domain.UploadDiagnosisKeysUseCase.Params
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.exposurenotification.Failure
import org.covidwatch.android.functional.Either
import timber.log.Timber
import java.security.SecureRandom

class UploadDiagnosisKeysUseCase(
    private val enManager: ExposureNotificationManager,
    private val diagnosisRepository: PositiveDiagnosisRepository,
    private val countryCodeRepository: CountryCodeRepository,
    private val verificationManager: DiagnosisVerificationManager,
    private val enConverter: EnConverter,
    private val uriManager: UriManager,
    private val appPackageName: String,
    private val random: SecureRandom,
    private val encoding: BaseEncoding,
    dispatchers: AppCoroutineDispatchers
) : UseCase<Unit, Params>(dispatchers) {

    private val paddingSizeMin = 1024
    private val paddingSizeMax = 2048

    override suspend fun run(params: Params?): Either<Failure, Unit> {
        params ?: return Either.Left(Failure.EnStatus.Failed)
        val verificationData = params.report.verificationData ?: return Either.Left(
            Failure.Internal("No verification data provided in params for ${javaClass.simpleName}")
        )

        val token = verificationData.token ?: return Either.Left(
            Failure.Internal("Token is null. Can't certificate an upload of a diagnosis")
        )

        enManager.isEnabled().apply {
            success { enabled ->
                if (!enabled) {
                    Timber.d("Can't start ${javaClass.simpleName}. EN is not enabled")
                    return Either.Left(Failure.EnStatus.ServiceDisabled)
                }
            }
            failure {
                Timber.d("Can't start ${javaClass.simpleName}. EN is not enabled")
                return Either.Left(it)
            }
        }
        Timber.d("Start ${javaClass.simpleName}")
        try {
            val diagnosisKeys = params.keys
                .map {
                    enConverter.diagnosisKey(
                        it,
                        verificationData.symptomsStartDate,
                        verificationData.testDate,
                        verificationData.possibleInfectionDate
                    )
                }
                .filter { it.transmissionRisk != 0 }

            Timber.d("Diagnosis Keys ${diagnosisKeys.joinToString()}")

            val regions = countryCodeRepository.exposureRelevantCountryCodes()
            val uploadEndpoints = uriManager.uploadUris(regions)

            val certificate: String
            val hmacKey: ByteArray
            val verifiedDiagnosis: PositiveDiagnosisReport

            // Check if we certificated the token before and reuse certificate
            if (verificationData.verificationCertificate != null && verificationData.hmacKey != null) {
                certificate = verificationData.verificationCertificate
                hmacKey = verificationData.hmacKey

                verifiedDiagnosis = params.report.copy(verified = true)
            } else { // otherwise certificate the token
                val verificationCertificate = verificationManager.certificate(
                    token,
                    diagnosisKeys
                )

                verifiedDiagnosis = params.report.copy(
                    verified = true,
                    verificationData = verificationData.copy(
                        hmacKey = verificationCertificate.hmacKey,
                        verificationCertificate = verificationCertificate.certificate
                    )
                )

                certificate = verificationCertificate.certificate
                hmacKey = verificationCertificate.hmacKey
            }

            diagnosisRepository.updatePositiveDiagnosisReport(verifiedDiagnosis)

            val positiveDiagnosis = PositiveDiagnosis(
                temporaryExposureKeys = diagnosisKeys,
                regions = regions,
                appPackageName = appPackageName,
                verificationPayload = certificate,
                hmacKey = encoding.encode(hmacKey),
                padding = randomPadding()
            )

            Timber.d("Upload positive diagnosis: $positiveDiagnosis")
            uploadEndpoints.forEach { url ->
                diagnosisRepository.uploadDiagnosisKeys(url, positiveDiagnosis)
            }

            diagnosisRepository.updatePositiveDiagnosisReport(verifiedDiagnosis.copy(uploaded = true))
            Timber.d("Uploaded positive diagnosis")
            return Either.Right(Unit)
        } catch (e: Exception) {
            Timber.d("Failed to upload positive diagnosis")
            Timber.e(e)
            return Either.Left(Failure(e))
        }
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
        val keys: List<TemporaryExposureKey>,
        val report: PositiveDiagnosisReport
    )
}