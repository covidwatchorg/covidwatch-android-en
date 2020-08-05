package org.covidwatch.android.ui.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import org.covidwatch.android.data.exposureinformation.ExposureInformationRepository
import org.covidwatch.android.data.pref.PreferenceStorage
import org.covidwatch.android.ui.BaseViewModel

class MenuViewModel(
    prefs: PreferenceStorage,
    exposureInformationRepository: ExposureInformationRepository
) :
    BaseViewModel() {

    private val _highRiskExposure = exposureInformationRepository.exposureInformation().map {
        val maxRisk = it.maxBy { exposure ->
            exposure.totalRiskScore
        } ?: return@map false

        maxRisk.highRisk
    }

    val regionDisabled = prefs.observableRegion.map { it.isDisabled }
    val highRiskExposure: LiveData<Boolean> = _highRiskExposure
}