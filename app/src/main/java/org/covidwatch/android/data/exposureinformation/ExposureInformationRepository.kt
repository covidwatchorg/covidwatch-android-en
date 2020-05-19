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
import org.covidwatch.android.data.pref.SharedPreferenceStorage


class ExposureInformationRepository(private val local: ExposureInformationLocalSource) {
    suspend fun saveExposureInformation(exposureInformation: List<CovidExposureInformation>) {
        local.saveExposureInformation(exposureInformation)
    }

    //TODO: Replace with real implementation
    /*
    fun exposureInformation() = mutableLiveData(
        List(10) {
            RandomEnObjects.exposureInformation.toCovidExposureInformation()
        }
    )
     */

    fun exposureInformation(): LiveData<List<CovidExposureInformation>> {
        return local.exposureInformation()
    }

    fun randomExposureInformation(): List<CovidExposureInformation> {
        return local.randomExposureInformation()
    }

    fun addFakeItem(context: Context){
        var returnExposureInformationList : List<CovidExposureInformation>
        val covidExposureInformation: CovidExposureInformation =
            RandomEnObjects.exposureInformation.toCovidExposureInformation()
        var exposureInformationList: List<CovidExposureInformation> =
            listOf(covidExposureInformation)

        GlobalScope.io {
            returnExposureInformationList = saveOneGetAll(exposureInformationList)
            //sum up risk exposures from returnExposureInformationList and pass to saveExposureSummaryInPreferences
            saveExposureSummaryInPreferences(context,covidExposureInformation,returnExposureInformationList.size)
        }
    }

    //Save the new exposureInformation object to the database
    //Read all the exposureInformation objects from the database into a list
    suspend private fun saveOneGetAll(
        exposureInformationList: List<CovidExposureInformation>): List<CovidExposureInformation>
    {
        saveExposureInformation(exposureInformationList)
        var newExposureInformationList: List<CovidExposureInformation>
        newExposureInformationList = randomExposureInformation()
        return newExposureInformationList
    }

    fun saveExposureSummaryInPreferences(
        context: Context,
        covidExposureInformation: CovidExposureInformation,
        matchedKeyCount: Int)
    {
        val exposureSummary: ExposureSummary = RandomEnObjects.exposureSummary
        val attenuationDurations: IntArray = intArrayOf(1)
        attenuationDurations[0] = covidExposureInformation.attenuationValue
        val sharedPreferences = SharedPreferenceStorage(context)
        sharedPreferences.exposureSummary = CovidExposureSummary(
            exposureSummary.daysSinceLastExposure,
            matchedKeyCount,
            exposureSummary.maximumRiskScore,
            attenuationDurations,
            covidExposureInformation.totalRiskScore
        )
        RandomEnObjects.retrieved = true
    }

}