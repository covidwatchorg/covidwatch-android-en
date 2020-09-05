package org.covidwatch.android.data.model

import java.time.Instant

data class RiskMetrics(
    val riskLevel: Double,
    val leastRecentSignificantExposureDate: Instant?,
    val mostRecentSignificantExposureDate: Instant?
)