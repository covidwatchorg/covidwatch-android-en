package org.covidwatch.android.data

import com.google.android.gms.nearby.exposurenotification.TemporaryExposureKey

data class PositiveDiagnosis(
    val temporaryExposureKeys: List<DiagnosisKey>,
    val regions: List<String>,
    val appPackageName: String,
    val platform: String,
    val verificationPayload: String,
    val deviceVerificationPayload: String,
    val padding: String
)

class DiagnosisKey(
    val key: ByteArray,
    val rollingStartNumber: Int,
    val transmissionRisk: Int,
    val rollingPeriod: Int
)

fun DiagnosisKey.asTemporaryExposureKey(): TemporaryExposureKey =
    TemporaryExposureKey.TemporaryExposureKeyBuilder()
        .setKeyData(key)
        .setRollingStartIntervalNumber(rollingStartNumber)
        .setTransmissionRiskLevel(transmissionRisk)
        .build()

fun TemporaryExposureKey.asDiagnosisKey() = DiagnosisKey(
    keyData,
    rollingStartIntervalNumber,
    transmissionRiskLevel,
    rollingPeriod
)