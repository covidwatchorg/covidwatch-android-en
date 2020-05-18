package org.covidwatch.android.data.exposureinformation

import androidx.lifecycle.LiveData
import org.covidwatch.android.data.CovidExposureInformation
import org.covidwatch.android.data.toCovidExposureInformation
import org.covidwatch.android.exposurenotification.RandomEnObjects
import org.covidwatch.android.extension.mutableLiveData

class ExposureInformationRepository(private val local: ExposureInformationLocalSource) {
    suspend fun saveExposureInformation(exposureInformation: List<CovidExposureInformation>) {
        local.saveExposureInformation(exposureInformation)
    }

    //TODO: Replace with real implementation
    fun exposureInformation() = mutableLiveData(
        List(10) {
            RandomEnObjects.exposureInformation.toCovidExposureInformation()
        }
    )

    fun realExposureInformation(): List<CovidExposureInformation> {
        return local.exposureInformation()
    }

}