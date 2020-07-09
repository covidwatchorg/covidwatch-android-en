package org.covidwatch.android.data.keyfile

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.covidwatch.android.data.converter.FileConverter
import java.io.File
import java.util.*

@Entity(tableName = "key_file")
@TypeConverters(value = [FileConverter::class])
data class KeyFile(
    val region: String,
    val batch: Int,
    val key: File,
    val url: String,
    val providedTime: Date = Date(),
    @PrimaryKey
    val id: String = url.split("/").last().removeSuffix(".zip")
)