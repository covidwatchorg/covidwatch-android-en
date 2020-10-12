package org.covidwatch.android.extension

import java.time.Instant
import java.time.temporal.ChronoUnit

fun Instant.plusDays(daysToAdd : Long) : Instant = plus(daysToAdd, ChronoUnit.DAYS)

fun Instant.minusDays(daysToSubtract : Long) : Instant = minus(daysToSubtract, ChronoUnit.DAYS)
