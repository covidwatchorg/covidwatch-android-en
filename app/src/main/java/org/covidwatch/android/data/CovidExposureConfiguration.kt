package org.covidwatch.android.data

import com.google.android.gms.nearby.exposurenotification.ExposureConfiguration
import com.google.gson.annotations.Expose

@Suppress("ArrayInDataClass")
data class CovidExposureConfiguration(
    @Expose
    val minimumRiskScore: Int,
    @Expose
    val attenuationScores: IntArray,
    @Expose
    val attenuationWeight: Int,
    @Expose
    val daysSinceLastExposureScores: IntArray,
    @Expose
    val daysSinceLastExposureWeight: Int,
    @Expose
    val durationScores: IntArray,
    @Expose
    val durationWeight: Int,
    @Expose
    val transmissionRiskScores: IntArray,
    @Expose
    val transmissionRiskWeight: Int,
    @Expose
    val durationAtAttenuationThresholds: IntArray
)

fun ExposureConfiguration.asCovidExposureConfiguration() = CovidExposureConfiguration(
    minimumRiskScore,
    attenuationScores,
    attenuationWeight,
    daysSinceLastExposureScores,
    daysSinceLastExposureWeight,
    durationScores,
    durationWeight,
    transmissionRiskScores,
    transmissionRiskWeight,
    durationAtAttenuationThresholds
)