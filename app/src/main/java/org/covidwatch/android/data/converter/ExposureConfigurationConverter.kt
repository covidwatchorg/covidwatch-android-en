package org.covidwatch.android.data.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import org.covidwatch.android.data.CovidExposureConfiguration

class ExposureConfigurationConverter {
    private val gson = Gson()

    @TypeConverter
    fun toExposureConfiguration(configurationJson: String?): CovidExposureConfiguration? =
        gson.fromJson(configurationJson, CovidExposureConfiguration::class.java)

    @TypeConverter
    fun fromExposureConfiguration(configuration: CovidExposureConfiguration?): String =
        gson.toJson(configuration)
}