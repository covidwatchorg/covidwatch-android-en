package org.covidwatch.android.data.exposureinformation

import androidx.lifecycle.LiveData
import org.covidwatch.android.data.CovidExposureInformation

class ExposureInformationLocalSource(private val dao: ExposureInformationDao) {
    suspend fun saveExposureInformation(exposureInformation: List<CovidExposureInformation>) {
        dao.saveExposureInformation(exposureInformation)
    }

    fun exposureInformation(): LiveData<List<CovidExposureInformation>> =
        dao.exposureInformation()

    suspend fun reset() {
        dao.reset()
    }
}