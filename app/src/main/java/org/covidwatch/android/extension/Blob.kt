package org.covidwatch.android.extension

import androidx.core.text.HtmlCompat
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.util.*

fun Date.toLocalDate(): LocalDate {
    val calendar = Calendar.getInstance().also {
        it.time = this
    }
    return LocalDate.of(
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH) + 1, // Calendar month is from 0 LocalDate is from 1 :shrug:
        calendar.get(Calendar.DAY_OF_MONTH)
    )
}

fun Date.daysTo(anotherDate: Date) =
    Period.between(this.toLocalDate(), anotherDate.toLocalDate()).days

fun Instant.daysTo(anotherDate: Instant) =
    Period.between(LocalDate.from(this), LocalDate.from(anotherDate)).days

fun String.fromHtml() = HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT)