package org.covidwatch.android.data

class CovidExposureSummary(
    val daySinceLastExposure: Int,
    val matchedKeyCount: Int,
    val maximumRiskScore: Int,
    val attenuationDurationsInMinutes: IntArray,
    val summationRiskScore: Int
)
