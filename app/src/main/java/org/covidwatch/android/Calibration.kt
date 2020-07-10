package org.covidwatch.android

val attenuationDurationThresholds: List<IntArray>
    get() {
        val startThreshold = intArrayOf(30, 33)
        return (0..66 step 3).map { i -> startThreshold.map { it + i }.toIntArray() }
    }

