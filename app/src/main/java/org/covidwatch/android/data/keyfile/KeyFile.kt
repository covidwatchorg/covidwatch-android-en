package org.covidwatch.android.data.keyfile

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.covidwatch.android.data.converter.FileConverter
import org.covidwatch.android.data.positivediagnosis.fileId
import java.io.File
import java.time.Instant

@Entity(tableName = "key_file")
@TypeConverters(value = [FileConverter::class])
data class KeyFile(
    val region: String,
    val batch: Int,
    val key: File,
    val url: String,
    val providedTime: Instant = Instant.now(),
    // TODO: 10.07.2020 Use url as primary key? This table won't be big most likely
    @PrimaryKey
    val id: String = url.fileId
)