package org.covidwatch.android.ui.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import org.covidwatch.android.data.exposureinformation.ExposureInformationRepository
import org.covidwatch.android.ui.BaseViewModel

class MenuViewModel(
    exposureInformationRepository: ExposureInformationRepository
) :
    BaseViewModel() {

    private val _highRiskExposure = exposureInformationRepository.exposureInformation().map {
        val maxRisk = it.maxBy { exposure ->
            exposure.totalRiskScore
        } ?: return@map false

        maxRisk.highRisk
    }

    val highRiskExposure: LiveData<Boolean>
        get() = _highRiskExposure
}