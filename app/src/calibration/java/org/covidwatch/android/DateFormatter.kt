package org.covidwatch.android

import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {
    private const val DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss"
    private val dateAndTime = SimpleDateFormat(DATE_TIME_PATTERN, Locale.US)

    @JvmStatic
    fun format(time: Date?): String = time?.let { dateAndTime.format(it) } ?: ""
}