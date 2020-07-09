package org.covidwatch.android.data.converter

import androidx.room.TypeConverter
import java.io.File

class FileConverter {
    @TypeConverter
    fun toFile(url: String?) = url?.let { File(it) }

    @TypeConverter
    fun fromFile(file: File?) = file?.absolutePath
}