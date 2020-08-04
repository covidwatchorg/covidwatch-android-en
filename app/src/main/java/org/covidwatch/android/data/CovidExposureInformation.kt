package org.covidwatch.android.data

import androidx.annotation.StringRes
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.Expose
import org.covidwatch.android.R
import org.covidwatch.android.data.converter.AttenuationDurationsConverter
import org.covidwatch.android.data.converter.ExposureConfigurationConverter
import java.io.Serializable
import java.time.Instant

@Entity(tableName = "exposure_information")
@TypeConverters(value = [AttenuationDurationsConverter::class, ExposureConfigurationConverter::class])
data class CovidExposureInformation(
    @Expose
    val date: Instant,
    @Expose
    val duration: Int,
    @Expose
    val attenuationValue: Int,
    @Expose
    val transmissionRiskLevel: Int,
    @Expose
    val totalRiskScore: RiskScore,
    @Expose
    val attenuationDurations: List<Int>,
    @PrimaryKey
    val id: Long
) : Serializable {

    // TODO: 17.06.2020 Remove this hacks after calibration is done or move to calibration specific
    // source folders
    var exposureConfiguration: CovidExposureConfiguration? = null
        set(value) {
            field = value
            attenuationDurationThresholds = exposureConfiguration?.durationAtAttenuationThresholds
        }

    @Expose
    @Ignore
    var attenuationDurationThresholds: IntArray? = null

    @Ignore
    @StringRes
    val howClose = when (attenuationValue) {
        in 0..100 -> R.string.far_exposure_distance
        in 101..200 -> R.string.close_exposure_distance
        else -> R.string.near_exposure_distance
    }

    @Ignore
    val riskScoreLevel = totalRiskScore.level

    @Ignore
    val highRisk = riskScoreLevel == RiskLevel.HIGH
}