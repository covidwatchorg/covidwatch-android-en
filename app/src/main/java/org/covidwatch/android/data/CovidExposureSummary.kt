package org.covidwatch.android.data

import com.google.android.gms.nearby.exposurenotification.ExposureSummary

class CovidExposureSummary(
    val daySinceLastExposure: Int,
    val matchedKeyCount: Int,
    val maximumRiskScore: RiskScore,
    val attenuationDurationsInMinutes: IntArray,
    // TODO: 09.06.2020 Check if summation risk score complies the same logic as all risk scores
    val summationRiskScore: RiskScore,
    val modifiedTime: Long = System.currentTimeMillis()
)

fun ExposureSummary.asCovidExposureSummary() = CovidExposureSummary(
    daysSinceLastExposure,
    matchedKeyCount,
    (maximumRiskScore * 8.0 / 4096).toInt(),
    attenuationDurationsInMinutes,
    (summationRiskScore * 8.0 / 4096).toInt()
)