package org.covidwatch.android.data.exposureinformation

import androidx.lifecycle.LiveData
import org.covidwatch.android.data.CovidExposureInformation


class ExposureInformationRepository(
    private val local: ExposureInformationLocalSource
) {
    suspend fun saveExposureInformation(exposureInformation: List<CovidExposureInformation>) {
        local.saveExposureInformation(exposureInformation)
    }

    fun exposureInformation(): LiveData<List<CovidExposureInformation>> {
        return local.exposureInformation()
    }

    suspend fun reset() {
        local.reset()
    }
}