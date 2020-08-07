package org.covidwatch.android.data.exposureinformation

import androidx.lifecycle.LiveData
import kotlinx.coroutines.withContext
import org.covidwatch.android.data.CovidExposureInformation
import org.covidwatch.android.domain.AppCoroutineDispatchers
import java.time.Instant


class ExposureInformationRepository(
    private val local: ExposureInformationLocalSource,
    private val dispatchers: AppCoroutineDispatchers
) {

    suspend fun saveExposureInformation(exposureInformation: List<CovidExposureInformation>) =
        withContext(dispatchers.io) {
            local.saveExposureInformation(exposureInformation)
        }

    fun exposureInformation(): LiveData<List<CovidExposureInformation>> {
        return local.exposureInformation()
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