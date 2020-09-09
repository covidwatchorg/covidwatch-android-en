package org.covidwatch.android.data.model

data class CovidScanInstance(
    val typicalAttenuation: Int,
    val minAttenuation: Int,
    val secondsSinceLastScan: Int
)