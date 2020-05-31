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
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.exposurenotification.ExposureNotificationManager.Companion.PERMISSION_START_REQUEST_CODE
import org.covidwatch.android.functional.Either
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

    val exposureInfo: LiveData<List<CovidExposureInformation>> =
        exposureInformationRepository.exposureInformation()

    val lastExposureTime = preferenceStorage.observableExposureSummary.map { it.modifiedTime }

    fun start() {
        viewModelScope.launch {
            _exposureNotificationEnabled.value = enManager.isEnabled().result()

            updateExposureInformationUseCase(this)
        }
    }

    fun enableExposureNotification(enable: Boolean) {
        viewModelScope.launch {
            val isEnabled = _exposureNotificationEnabled.value ?: false

            when {
                enable && !isEnabled -> withPermission(PERMISSION_START_REQUEST_CODE) { enManager.start() }
                !enable && isEnabled -> enManager.stop()
            }
        }
    }

    fun showExposureDetails(exposureInformation: CovidExposureInformation) {
        _showExposureDetails.value = Event(exposureInformation)
    }

    private fun <R : ENStatus, L> Either<R, L>.result(): L? {
        left?.let { handleError(it) }
        return right
    }

    private fun handleError(status: ENStatus?) {
        when (status) {
            ENStatus.FailedRejectedOptIn -> TODO()
            ENStatus.FailedServiceDisabled -> TODO()
            ENStatus.FailedBluetoothScanningDisabled -> TODO()
            ENStatus.FailedTemporarilyDisabled -> TODO()
            ENStatus.FailedInsufficientStorage -> TODO()
            ENStatus.Failed -> TODO()
        }
    }
}
