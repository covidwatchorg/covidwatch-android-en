package org.covidwatch.android.data.model

import com.google.gson.annotations.SerializedName
import org.covidwatch.android.data.ArizonaRiskModelConfiguration

data class Region(
    val id: Int,
    val name: String,
    val isDisabled: Boolean = false,

    val nextStepsNoSignificantExposure: List<NextStep>,
    val nextStepsSignificantExposure: List<NextStep>,

    val nextStepsVerifiedPositive: List<NextStep>,
    val nextStepsVerificationCode: List<NextStep>,
    val exposureConfiguration: ExposureConfiguration = ExposureConfiguration(),

    @SerializedName("azRiskModelConfiguration")
    val riskModelConfiguration: RiskModelConfiguration = ArizonaRiskModelConfiguration(),

    /**
     * How many days since the last significant exposure considered to be a high risk exposure
     */
    val recentExposureDays: Int
)

class ExposureConfiguration(
    val minimumRiskScore: Int = 1,
    val attenuationDurationThresholds: IntArray = intArrayOf(50, 70),
    val attenuationLevelValues: IntArray = intArrayOf(1, 1, 1, 1, 1, 1, 1, 1),
    val daysSinceLastExposureLevelValues: IntArray = intArrayOf(1, 1, 1, 1, 1, 1, 1, 1),
    val durationLevelValues: IntArray = intArrayOf(1, 1, 1, 1, 1, 1, 1, 1),
    val transmissionRiskLevelValues: IntArray = intArrayOf(1, 1, 1, 1, 1, 1, 1, 1)
)

fun ExposureConfiguration.asCovidExposureConfiguration() =
    CovidExposureConfiguration(
        if (minimumRiskScore == 0) 1 else minimumRiskScore,
        attenuationLevelValues,
        daysSinceLastExposureLevelValues,
        durationLevelValues,
        transmissionRiskLevelValues,
        attenuationDurationThresholds
    )

data class NextStep(
    val type: NextStepType?,
    val description: String,
    val url: String? = null
)

enum class NextStepType {
    @SerializedName("0")
    INFO,

    @SerializedName("1")
    PHONE,

    @SerializedName("2")
    WEBSITE,

    @SerializedName("3")
    SHARE,

    @SerializedName("4")
    SELECT_REGION
}

data class Regions(val regions: List<Region>)