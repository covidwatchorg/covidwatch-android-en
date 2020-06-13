package org.covidwatch.android.data.converter

import androidx.room.TypeConverter

class AttenuationDurationsConverter {

    @TypeConverter
    fun toString(durations: List<Int>) = durations.joinToString()

    @TypeConverter
    fun fromString(durations: String) = durations.split(", ").map { it.toInt() }
}
