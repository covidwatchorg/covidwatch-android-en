package org.covidwatch.android.data.model

import com.google.android.gms.nearby.exposurenotification.ExposureConfiguration

class CovidExposureConfiguration(
    val minimumRiskScore: Int,
    val attenuationScores: IntArray,
    val daysSinceLastExposureScores: IntArray,
    val durationScores: IntArray,
    val transmissionRiskScores: IntArray,
    val durationAtAttenuationThresholds: IntArray,

    val attenuationWeight: Int? = null,
    val daysSinceLastExposureWeight: Int? = null,
    val durationWeight: Int? = null,
    val transmissionRiskWeight: Int? = null
)

fun ExposureConfiguration.asCovidExposureConfiguration() =
    CovidExposureConfiguration(
        minimumRiskScore,
        attenuationScores,
        daysSinceLastExposureScores,
        durationScores,
        transmissionRiskScores,
        durationAtAttenuationThresholds,

        attenuationWeight,
        daysSinceLastExposureWeight,
        durationWeight,
        transmissionRiskWeight
    )

fun CovidExposureConfiguration.asExposureConfiguration(): ExposureConfiguration =
    ExposureConfiguration.ExposureConfigurationBuilder().let { builder ->
        builder.setMinimumRiskScore(minimumRiskScore)
        builder.setDurationAtAttenuationThresholds(*durationAtAttenuationThresholds)

        builder.setAttenuationScores(*attenuationScores)
        attenuationWeight?.let { builder.setAttenuationWeight(it) }

        builder.setDaysSinceLastExposureScores(*daysSinceLastExposureScores)
        daysSinceLastExposureWeight?.let { builder.setDaysSinceLastExposureWeight(it) }

        builder.setDurationScores(*durationScores)
        durationWeight?.let { builder.setDurationWeight(it) }

        builder.setTransmissionRiskScores(*transmissionRiskScores)
        transmissionRiskWeight?.let { builder.setTransmissionRiskWeight(it) }

        builder.build()
    }