package org.covidwatch.android.data.exposureinformation

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.GlobalScope
import org.covidwatch.android.R
import org.covidwatch.android.data.CovidExposureInformation
import org.covidwatch.android.data.toCovidExposureInformation
import org.covidwatch.android.exposurenotification.RandomEnObjects
import org.covidwatch.android.exposurenotification.TestExposureNotification
import org.covidwatch.android.extension.io
import org.covidwatch.android.extension.mutableLiveData
import androidx.navigation.fragment.findNavController


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
        val testExposureNotification = TestExposureNotification()
        val covidExposureInformation: CovidExposureInformation =
            RandomEnObjects.exposureInformation.toCovidExposureInformation()
        var exposureInformationList: List<CovidExposureInformation> =
            listOf(covidExposureInformation)

        GlobalScope.io {
            returnExposureInformationList = saveOneGetAll(exposureInformationList)
            //sum up risk exposures from returnExposureInformationList and pass to TestExposureNotification
            testExposureNotification.saveExposureSummaryInPreferences(context,covidExposureInformation,returnExposureInformationList.size)
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

}