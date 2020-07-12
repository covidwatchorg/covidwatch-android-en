package org.covidwatch.android.data

data class Region(
    val id: Int,
    val name: String,
    val logoTypeImageName: String,
    val logoImageName: String,
    val riskLowThreshold: Float,
    val riskHighThreshold: Float,
    val nextStepsRiskUnknown: List<NextStep>,
    val nextStepsRiskLow: List<NextStep>,
    val nextStepsRiskMedium: List<NextStep>,
    val nextStepsRiskHigh: List<NextStep>,
    val nextStepsRiskVerifiedPositive: List<NextStep>
)

data class NextStep(
    val type: NextStepType,
    val description: String,
    val url: String?
)

enum class NextStepType {
    INFO,
    PHONE,
    WEBSITE,
    GET_TESTED_DATES,
    SHARE
}

data class Regions(val regions: List<Region>)
