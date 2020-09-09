package org.covidwatch.android.data.exposurewindow

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.covidwatch.android.data.BaseDao
import org.covidwatch.android.data.model.CovidExposureWindow

@Dao
interface ExposureWindowDao : BaseDao<CovidExposureWindow> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addExposureWindows(exposureInformation: List<CovidExposureWindow>)

    @Query("SELECT * FROM exposure_window")
    fun exposuresLiveData(): LiveData<List<CovidExposureWindow>>

    @Query("SELECT * FROM exposure_window")
    suspend fun exposures(): List<CovidExposureWindow>

    @Query("DELETE FROM exposure_window")
    suspend fun reset()

    @Query("DELETE FROM exposure_window WHERE date < :date")
    suspend fun deleteOlderThan(date: Long)
}