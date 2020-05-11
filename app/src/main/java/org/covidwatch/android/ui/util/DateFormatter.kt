package org.covidwatch.android.ui.util

import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {
    private const val DATE_PATTERN = "MMM dd, yyyy"

    private var locale = Locale.getDefault()
    private var date = SimpleDateFormat(DATE_PATTERN, locale)
        get() {
            if (locale == Locale.getDefault()) {
                return field
            }
            return SimpleDateFormat(DATE_PATTERN, locale).also { field = it }
        }

    fun format(time: Long?): String = date.format(time)
}