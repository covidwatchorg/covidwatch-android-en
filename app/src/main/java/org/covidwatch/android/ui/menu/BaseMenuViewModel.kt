package org.covidwatch.android.ui.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import org.covidwatch.android.data.RiskLevel
import org.covidwatch.android.data.risklevel.RiskLevelRepository
import org.covidwatch.android.ui.BaseViewModel

class MenuViewModel(
    riskLevelRepository: RiskLevelRepository
) : BaseViewModel() {

    private val _highRiskExposure =
        riskLevelRepository.riskLevel.asLiveData().map { it == RiskLevel.HIGH }

    val highRiskExposure: LiveData<Boolean>
        get() = _highRiskExposure
}