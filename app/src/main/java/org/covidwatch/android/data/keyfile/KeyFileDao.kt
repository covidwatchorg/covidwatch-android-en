package org.covidwatch.android.data.keyfile

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.covidwatch.android.data.BaseDao

@Dao
interface KeyFileDao : BaseDao<KeyFile> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insert(obj: KeyFile)

    @Query("SELECT * FROM key_file")
    suspend fun keyFiles(): List<KeyFile>

    @Query("DELETE FROM key_file")
    suspend fun reset()
}