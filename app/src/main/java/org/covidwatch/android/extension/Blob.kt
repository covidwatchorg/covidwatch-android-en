package org.covidwatch.android.extension

import androidx.core.text.HtmlCompat
import java.time.LocalDate
import java.time.Period
import java.util.*

fun Date.toLocalDate(): LocalDate {
    val calendar = Calendar.getInstance().also {
        it.time = this
    }
    return LocalDate.of(
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
}

fun Date.daysTo(anotherDate: Date) =
    Period.between(this.toLocalDate(), anotherDate.toLocalDate()).days

fun String.fromHtml() = HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT)