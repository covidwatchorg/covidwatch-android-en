package org.covidwatch.android.data.exposurewindow

import org.covidwatch.android.data.model.CovidExposureWindow

class ExposureWindowLocalSource(private val dao: ExposureWindowDao) {
    suspend fun saveExposures(exposures: List<CovidExposureWindow>) {
        dao.addExposureWindows(exposures)
    }

    fun exposuresLiveData() = dao.exposuresLiveData()

    suspend fun exposures() = dao.exposures()

    suspend fun reset() {
        dao.reset()
    }

    suspend fun deleteOlderThan(date: Long) = dao.deleteOlderThan(date)
}