package org.covidwatch.android.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.nearby.exposurenotification.TemporaryExposureKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "positive_diagnosis_report")
data class PositiveDiagnosisReport(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val verified: Boolean = false,
    val uploaded: Boolean = false,
    val reportDate: Date = Date(),
    @Embedded
    val verificationData: PositiveDiagnosisVerification? = null
)

data class PositiveDiagnosis(
    val temporaryExposureKeys: List<DiagnosisKey>,
    val regions: List<String>,
    val appPackageName: String,
    val verificationPayload: String,
    @SerializedName("hmackey")
    val hmacKey: String,
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
    val verificationTestCode: String = "",
    val symptomsStartDate: Long? = null,
    val noSymptoms: Boolean = false,
    val testDate: Date? = null,
    val testType: String? = null,
    val token: String? = null,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val hmacKey: ByteArray? = null,
    val verificationCertificate: String? = null
) {
    val readyToSubmit: Boolean
        get() = verificationTestCode.trim().isNotEmpty()
}