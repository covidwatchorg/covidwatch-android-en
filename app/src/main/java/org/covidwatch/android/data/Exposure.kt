package org.covidwatch.android.data

import org.covidwatch.android.data.RiskScoreLevel.*

typealias RiskScore = Int

enum class RiskScoreLevel {
    HIGH,
    MEDIUM,
    LOW,
    NONE
}

val RiskScore.level: RiskScoreLevel
    get() {
        return when (this) {
            0 -> NONE
            in 1..2 -> LOW
            in 3..5 -> MEDIUM
            in 6..8 -> HIGH
            else -> HIGH
        }
    }