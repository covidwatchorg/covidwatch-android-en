package org.covidwatch.android.exposurenotification

import android.content.Context

import org.covidwatch.android.data.CovidExposureInformation
import org.covidwatch.android.data.CovidExposureSummary
import org.covidwatch.android.data.pref.SharedPreferenceStorage
import com.google.android.gms.nearby.exposurenotification.*


class TestExposureNotification {

    fun saveExposureSummaryInPreferences(context: Context, covidExposureInformation: CovidExposureInformation){
        val exposureSummaryRandom: ExposureSummary = RandomEnObjects.exposureSummary
        RandomEnObjects.retrieved = true
        var sharedPreferences: SharedPreferenceStorage = SharedPreferenceStorage(context)
        val daysSinceLastExposure: Int = exposureSummaryRandom.daysSinceLastExposure
        //TODO: get total from database
        val matchedKeyCount = 1
        val maximumRiskScore = exposureSummaryRandom.maximumRiskScore
        val attenuationDurations: IntArray = intArrayOf(1)
        attenuationDurations[0] = covidExposureInformation.attenuationValue
        val summationRiskScore = covidExposureInformation.totalRiskScore
        var covidExposureSummary: CovidExposureSummary = CovidExposureSummary(
            daysSinceLastExposure, matchedKeyCount, maximumRiskScore, attenuationDurations, summationRiskScore )
        val prefs = context.applicationContext.getSharedPreferences(
            "exposure_summary",
            Context.MODE_PRIVATE
        )
        with (prefs.edit()) {
            sharedPreferences.exposureSummary = covidExposureSummary
            commit()

        }
        val exposureSummary: CovidExposureSummary = sharedPreferences.exposureSummary
    }
}