package org.covidwatch.android.data

import java.util.*

@Suppress("ArrayInDataClass")
data class CovidExposureSummary(
    val daySinceLastExposure: Int,
    val matchedKeyCount: Int,
    val maximumRiskScore: RiskScore,
    val attenuationDurationsInMinutes: IntArray,
    // TODO: 09.06.2020 Check if summation risk score complies the same logic as all risk scores
    val summationRiskScore: RiskScore,
    val modifiedTime: Date = Date()
)