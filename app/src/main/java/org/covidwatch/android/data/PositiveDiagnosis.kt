package org.covidwatch.android.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.nearby.exposurenotification.TemporaryExposureKey

@Entity(tableName = "positive_diagnosis_report")
data class PositiveDiagnosisReport(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val verified: Boolean,
    val reportDate: Long
)

data class PositiveDiagnosis(
    val temporaryExposureKeys: List<DiagnosisKey>,
    val regions: List<String>,
    val appPackageName: String,
    val platform: String,
    val verificationPayload: String,
    val deviceVerificationPayload: String,
    val padding: String
)

@Suppress("ArrayInDataClass")
data class DiagnosisKey(
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