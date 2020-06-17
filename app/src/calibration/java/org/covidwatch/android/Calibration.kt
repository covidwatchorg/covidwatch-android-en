package org.covidwatch.android

val attenuationDurationThresholds: List<IntArray>
    get() {
        var start = intArrayOf(30, 33)
        val tmp = mutableListOf<IntArray>()
        intArrayOf(30, 33)
        for (i in 0..66 step 3) {
            start = start.map { it + 3 }.toIntArray()
            tmp.add(start)
        }
        return tmp
    }

