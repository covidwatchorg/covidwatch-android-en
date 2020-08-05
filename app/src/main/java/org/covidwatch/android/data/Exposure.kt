package org.covidwatch.android.data

import org.covidwatch.android.data.RiskLevel.HIGH
import org.covidwatch.android.data.RiskLevel.LOW

typealias RiskScore = Int

enum class RiskLevel {
    VERIFIED_POSITIVE,
    HIGH,
    LOW,
    DISABLED
}

val RiskScore.level: RiskLevel
    get() {
        return when (this) {
            in 0..5 -> LOW
            in 6..8 -> HIGH
            else -> HIGH
        }
    }