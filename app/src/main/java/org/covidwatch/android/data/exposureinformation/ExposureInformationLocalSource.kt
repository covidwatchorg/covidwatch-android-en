package org.covidwatch.android.data.exposureinformation

import androidx.lifecycle.LiveData
import org.covidwatch.android.data.AppDatabase
import org.covidwatch.android.data.CovidExposureInformation

class ExposureInformationLocalSource(private val database: AppDatabase) {
    suspend fun saveExposureInformation(exposureInformation: List<CovidExposureInformation>) {
        database.exposureInformationDao().saveExposureInformation(exposureInformation)
    }

    //fun exposureInformation(): LiveData<List<CovidExposureInformation>> =
    fun exposureInformation(): List<CovidExposureInformation> =
        database.exposureInformationDao().exposureInformation()
}