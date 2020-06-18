package org.covidwatch.android

val attenuationDurationThresholds: List<IntArray>
    get() {
        var threshold = intArrayOf(30, 33)
        val thresholds = mutableListOf(threshold)

        for (i in 0..66 step 3) {
            threshold = threshold.map { it + 3 }.toIntArray()

            thresholds.add(threshold)
        }
        return thresholds
    }

