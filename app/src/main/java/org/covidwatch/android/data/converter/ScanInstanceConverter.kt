package org.covidwatch.android.data.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.covidwatch.android.data.model.CovidScanInstance

class ScanInstanceConverter {
    private val gson = Gson()
    private val type = object : TypeToken<List<CovidScanInstance>>() {}.type

    @TypeConverter
    fun toScanIntances(scanJson: String?): List<CovidScanInstance> = gson.fromJson(scanJson, type)

    @TypeConverter
    fun fromScanInstances(scans: List<CovidScanInstance>): String = gson.toJson(scans)
}