package org.covidwatch.android.extension

import java.time.Instant

// 1d * (24h/d) * (60m/h) * (60s/m)
fun Instant.plusDays(daysToAdd : Long) : Instant =
    plusSeconds(daysToAdd * 24 * 60 * 60)

fun Instant.minusDays(daysToSubtract : Long) : Instant = plusDays(-daysToSubtract)