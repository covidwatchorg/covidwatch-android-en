package org.covidwatch.android.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.nearby.exposurenotification.TemporaryExposureKey
import java.util.*

@Entity(tableName = "positive_diagnosis_report")
data class PositiveDiagnosisReport(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val verified: Boolean,
    val reportDate: Date,
    @Embedded
    val verificationData: PositiveDiagnosisVerification? = null
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
        .setRollingPeriod(rollingPeriod)
        .setRollingStartIntervalNumber(rollingStartNumber)
        .setTransmissionRiskLevel(transmissionRisk)
        .build()

fun TemporaryExposureKey.asDiagnosisKey() = DiagnosisKey(
    keyData,
    rollingStartIntervalNumber,
    transmissionRiskLevel,
    rollingPeriod
)

data class PositiveDiagnosisVerification(
    val verificationTestCode: String? = null,
    val symptomsStartDate: Long? = null,
    val noSymptoms: Boolean = false,
    val testedDate: Long? = null
) {
    val readyToSubmit: Boolean
        get() = verificationTestCode != null &&
                testedDate != null &&
                (symptomsStartDate != null || noSymptoms)
}