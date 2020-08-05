package org.covidwatch.android.extension

import androidx.core.text.HtmlCompat
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId

fun Instant.toLocalDate(): LocalDate {
    return atZone(ZoneId.of("UTC")).toLocalDate()
}

fun Instant.daysTo(anotherDate: Instant) =
    Period.between(this.toLocalDate(), anotherDate.toLocalDate()).days

fun String.fromHtml(flags: Int = HtmlCompat.FROM_HTML_MODE_COMPACT) =
    HtmlCompat.fromHtml(this, flags)