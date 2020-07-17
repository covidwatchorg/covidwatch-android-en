package org.covidwatch.android.data.exposureinformation

import kotlinx.coroutines.withContext
import org.covidwatch.android.data.CovidExposureInformation
import org.covidwatch.android.data.EnConverter
import org.covidwatch.android.domain.AppCoroutineDispatchers
import org.covidwatch.android.exposurenotification.RandomEnObjects
import org.covidwatch.android.extension.mutableLiveData
import org.koin.java.KoinJavaComponent.inject


class ExposureInformationRepository(
    private val local: ExposureInformationLocalSource,
    private val dispatchers: AppCoroutineDispatchers
) {
    private val arizonaEnConverter by inject(EnConverter::class.java)
    suspend fun saveExposureInformation(exposureInformation: List<CovidExposureInformation>) =
        withContext(dispatchers.io) {
            local.saveExposureInformation(exposureInformation)
        }

    //    fun exposureInformation(): LiveData<List<CovidExposureInformation>> {
//        return local.exposureInformation()
//    }
    fun exposureInformation() =
        mutableLiveData(
            listOf(
                arizonaEnConverter.covidExposureInformation(RandomEnObjects.exposureInformation),
                arizonaEnConverter.covidExposureInformation(RandomEnObjects.exposureInformation)
            )
        )

    suspend fun exposures() = withContext(dispatchers.io) {
        local.exposures()
    }

    suspend fun reset() = withContext(dispatchers.io) {
        local.reset()
    }
}