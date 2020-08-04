package org.covidwatch.android.ui.util

import org.covidwatch.android.extension.toLocalDate
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

object DateFormatter {
    private const val DATE_PATTERN = "MMM dd, yyyy"
    private var locale = Locale.getDefault()

    private var dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN, locale)
        get() {
            if (locale == Locale.getDefault()) {
                return field
            }
            locale = Locale.getDefault()
            return DateTimeFormatter.ofPattern(DATE_PATTERN, locale).also { field = it }
        }

    private var fullDateTimeFormatter =
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
        get() {
            if (locale == Locale.getDefault()) {
                return field
            }
            locale = Locale.getDefault()
            return field.withLocale(locale).also { field = it }
        }

    @JvmStatic
    fun format(time: Instant?): String =
        time?.let { dateTimeFormatter.format(it.toLocalDate()) } ?: ""

    @JvmStatic
    fun format(time: Long?): String =
        time?.let { dateTimeFormatter.format(Instant.ofEpochMilli(it).toLocalDate()) } ?: ""

    fun symptomDate(date: String?) =
        date?.takeIf { it.isNotEmpty() }
            ?.let { LocalDate.parse(date).atStartOfDay(ZoneId.of("UTC")) }
            ?.toInstant()

    @JvmStatic
    fun formatDateAndTime(time: Instant?): String =
        time?.let {
            fullDateTimeFormatter.format(
                ZonedDateTime.ofInstant(
                    it,
                    ZoneId.systemDefault()
                )
            )
        } ?: ""
}