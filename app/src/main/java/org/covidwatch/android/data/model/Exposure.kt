package org.covidwatch.android.data.model

import org.covidwatch.android.data.model.RiskLevel.HIGH
import org.covidwatch.android.data.model.RiskLevel.LOW

typealias RiskScore = Int

enum class RiskLevel {
    VERIFIED_POSITIVE,
    HIGH,
    LOW
}

val RiskScore.level: RiskLevel
    get() {
        return when (this) {
            in 0..5 -> LOW
            in 6..8 -> HIGH
            else -> HIGH
        }
    }