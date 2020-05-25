package org.covidwatch.android.data.exposureinformation

import android.content.Context
import androidx.lifecycle.LiveData
import com.google.android.gms.nearby.exposurenotification.ExposureSummary
import kotlinx.coroutines.GlobalScope
import org.covidwatch.android.R
import org.covidwatch.android.data.CovidExposureInformation
import org.covidwatch.android.data.toCovidExposureInformation
import org.covidwatch.android.exposurenotification.RandomEnObjects
import org.covidwatch.android.extension.io
import org.covidwatch.android.data.CovidExposureSummary
import org.covidwatch.android.data.pref.PreferenceStorage
import org.covidwatch.android.data.pref.SharedPreferenceStorage
import org.covidwatch.android.*


class ExposureInformationRepository(
    private val local: ExposureInformationLocalSource,
    private val preferences: PreferenceStorage
) {
    suspend fun saveExposureInformation(exposureInformation: List<CovidExposureInformation>) {
        local.saveExposureInformation(exposureInformation)
    }

    fun exposureInformation(): LiveData<List<CovidExposureInformation>> {
        return local.exposureInformation()
    }

    suspend fun exposureInformationList(): List<CovidExposureInformation> {
        return local.exposureInformationList()
    }

    fun addFakeItem() {
        GlobalScope.io {
            val exposureInformation: CovidExposureInformation =
                RandomEnObjects.exposureInformation.toCovidExposureInformation()
            var exposureInfoList: MutableList<CovidExposureInformation> = mutableListOf()
            exposureInfoList.add(exposureInformation)
            saveExposureInformation(exposureInfoList)
            saveExposureSummaryInPreferences(exposureInformation, exposureInformationList().size)
        }
    }

    fun saveExposureSummaryInPreferences(
        covidExposureInformation: CovidExposureInformation,
        matchedKeyCount: Int
    ) {
        val exposureSummary: ExposureSummary = RandomEnObjects.exposureSummary
        val attenuationDurations: IntArray = intArrayOf(1)
        attenuationDurations[0] = covidExposureInformation.attenuationValue
        preferences.exposureSummary = CovidExposureSummary(
            exposureSummary.daysSinceLastExposure,
            matchedKeyCount,
            exposureSummary.maximumRiskScore,
            attenuationDurations,
            covidExposureInformation.totalRiskScore
        )
    }

}