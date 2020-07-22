package org.covidwatch.android.ui.exposures

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.covidwatch.android.data.CovidExposureInformation
import org.covidwatch.android.data.exposureinformation.ExposureInformationRepository
import org.covidwatch.android.data.pref.PreferenceStorage
import org.covidwatch.android.domain.UpdateExposureInformationUseCase
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.exposurenotification.ExposureNotificationManager.Companion.PERMISSION_START_REQUEST_CODE
import org.covidwatch.android.ui.BaseViewModel
import org.covidwatch.android.ui.event.Event

class ExposuresViewModel(
    private val enManager: ExposureNotificationManager,
    private val updateExposureInformationUseCase: UpdateExposureInformationUseCase,
    preferenceStorage: PreferenceStorage,
    exposureInformationRepository: ExposureInformationRepository
) : BaseViewModel() {

    private val _exposureNotificationEnabled = MutableLiveData<Boolean>()
    val exposureNotificationEnabled: LiveData<Boolean> = _exposureNotificationEnabled

    private val _showExposureDetails = MutableLiveData<Event<CovidExposureInformation>>()
    val showExposureDetails: LiveData<Event<CovidExposureInformation>> = _showExposureDetails

    val exposureInfo: LiveData<List<Any>> =
        exposureInformationRepository.exposureInformation().map { exposures ->
            if (exposures.isNotEmpty()) exposures.sortedByDescending { it.date.time } + Footer
            else exposures
        }

    val lastExposureTime = preferenceStorage.observableExposureSummary.map { it.modifiedTime }

    fun start() {
        viewModelScope.launch {
            _exposureNotificationEnabled.value = isExposureNotificationEnabled()

            updateExposureInformationUseCase(this)
        }
    }

    fun enableExposureNotification(enable: Boolean) {
        viewModelScope.launch {
            val isEnabled = isExposureNotificationEnabled()

            when {
                enable && !isEnabled -> withPermission(PERMISSION_START_REQUEST_CODE) { enManager.start() }
                !enable && isEnabled -> enManager.stop()
            }
        }
    }

    private suspend fun isExposureNotificationEnabled() = enManager.isEnabled().result() ?: false

    object Footer
}
