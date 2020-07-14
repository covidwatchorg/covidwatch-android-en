package org.covidwatch.android.data

import org.covidwatch.android.data.RiskLevel.*

typealias RiskScore = Int

enum class RiskLevel {
    VERIFIED_POSITIVE,
    HIGH,
    MEDIUM,
    LOW,
    UNKNOWN
}

val RiskScore.level: RiskLevel
    get() {
        return when (this) {
            0 -> UNKNOWN
            in 1..2 -> LOW
            in 3..5 -> MEDIUM
            in 6..8 -> HIGH
            else -> HIGH
        }
    }