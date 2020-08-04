package org.covidwatch.android.data.converter

import androidx.room.TypeConverter
import java.time.Instant

class InstantConverter {
    @TypeConverter
    fun toInstant(time: Long?) = time?.let { Instant.ofEpochMilli(time) }

    @TypeConverter
    fun fromInstant(instant: Instant?) = instant?.toEpochMilli()
}