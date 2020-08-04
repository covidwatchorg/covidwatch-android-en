package org.covidwatch.android.ui.util

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

object DateFormatter {
    private const val DATE_PATTERN = "MMM dd, yyyy"
    private const val SYMPTOM_DATE_PATTERN = "yyyy-MM-dd"
    private const val DATE_TIME_PATTERN = "MMM dd, yyyy, hh:mm aa"

    private var locale = Locale.getDefault()

    private var dateFormat = SimpleDateFormat(DATE_PATTERN, locale)
        get() {
            if (locale == Locale.getDefault()) {
                return field
            }
            locale = Locale.getDefault()
            return SimpleDateFormat(DATE_PATTERN, locale).also { field = it }
        }

    private var dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN, locale)
        get() {
            if (locale == Locale.getDefault()) {
                return field
            }
            locale = Locale.getDefault()
            return DateTimeFormatter.ofPattern(DATE_PATTERN, locale).also { field = it }
        }

    private var fullDateTimeFormatter =
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM)
        get() {
            if (locale == Locale.getDefault()) {
                return field
            }
            locale = Locale.getDefault()
            return field.withLocale(locale).also { field = it }
        }

    private var symptomDateFormat = SimpleDateFormat(SYMPTOM_DATE_PATTERN, Locale.US)

    @JvmStatic
    fun format(time: Date?): String = time?.let { dateFormat.format(it) } ?: ""

    @JvmStatic
    fun format(time: Instant?): String =
        time?.let { dateTimeFormatter.format(ZonedDateTime.ofInstant(it, ZoneId.systemDefault())) }
            ?: ""

    @JvmStatic
    fun format(time: Long?): String =
        time?.let {
            dateTimeFormatter.format(
                ZonedDateTime.ofInstant(
                    Instant.ofEpochMilli(it),
                    ZoneId.systemDefault()
                )
            )
        } ?: ""

    fun symptomDate(date: String?) =
        date?.takeIf { it.isNotEmpty() }?.let { symptomDateFormat.parse(it) }

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