package org.covidwatch.android.data.exposurewindow

import androidx.lifecycle.LiveData
import kotlinx.coroutines.withContext
import org.covidwatch.android.data.model.CovidExposureWindow
import org.covidwatch.android.domain.AppCoroutineDispatchers
import java.time.Instant


class ExposureWindowRepository(
    private val local: ExposureWindowLocalSource,
    private val dispatchers: AppCoroutineDispatchers
) {

    suspend fun saveExposures(exposureInformation: List<CovidExposureWindow>) =
        withContext(dispatchers.io) {
            local.saveExposures(exposureInformation)
        }

    fun exposuresLiveData(): LiveData<List<CovidExposureWindow>> {
        return local.exposuresLiveData()
    }

    suspend fun exposures() = withContext(dispatchers.io) {
        local.exposures()
    }

    suspend fun reset() = withContext(dispatchers.io) {
        local.reset()
    }

    suspend fun deleteOlderThan(date: Instant) =
        withContext(dispatchers.io) { local.deleteOlderThan(date.toEpochMilli()) }
}