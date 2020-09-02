package org.covidwatch.android.data.diagnosiskeystoken

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.covidwatch.android.data.CovidExposureConfiguration
import org.covidwatch.android.data.converter.ExposureConfigurationConverter

@Entity(tableName = "diagnosis_keys_token")
@TypeConverters(ExposureConfigurationConverter::class)
data class DiagnosisKeysToken(
    @PrimaryKey val token: String,
    val exposureConfiguration: CovidExposureConfiguration,
    val providedTime: Long = System.currentTimeMillis(),
    val potentialExposure: Boolean = false
)
