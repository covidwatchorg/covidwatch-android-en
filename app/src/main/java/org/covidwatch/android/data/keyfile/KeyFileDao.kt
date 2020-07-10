package org.covidwatch.android.data.keyfile

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import org.covidwatch.android.data.BaseDao

@Dao
interface KeyFileDao : BaseDao<KeyFile> {
    @Query("SELECT * FROM key_file")
    suspend fun keyFiles(): List<KeyFile>
}