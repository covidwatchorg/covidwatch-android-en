package org.covidwatch.android.ui.exposures

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.covidwatch.android.data.CovidExposureInformation
import org.covidwatch.android.data.exposureinformation.ExposureInformationRepository
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.functional.Either

class ExposuresViewModel(
    private val enManager: ExposureNotificationManager,
    exposureInformationRepository: ExposureInformationRepository
) : ViewModel() {

    private val _exposureNotificationEnabled = MutableLiveData<Boolean>()
    val exposureNotificationEnabled: LiveData<Boolean> = _exposureNotificationEnabled

    val exposureInfo: LiveData<List<CovidExposureInformation>> =
        exposureInformationRepository.exposureInformation()

    fun start() {
        viewModelScope.launch {
            _exposureNotificationEnabled.value = enManager.isEnabled().result()
        }
    }

    fun toggleExposureNotifications() {
        viewModelScope.launch {
            // Check if we need to stop the service
            if (enManager.isEnabled().result() == true) {
                enManager.stop()
            } else { // Otherwise run the service
                enManager.start().result()
            }

            // Handle the case of is service running or not simply by asking the manager
            _exposureNotificationEnabled.value = enManager.isEnabled().result()
        }
    }

    fun showExposureDetails(exposureInformation: CovidExposureInformation) {
        TODO()
    }

    private fun <R : ENStatus, L> Either<R, L>.result(): L? {
        left?.let { handleError(it) }
        return right
    }

    private fun handleError(status: ENStatus?) {
        when (status) {
            ENStatus.SUCCESS -> TODO()
            ENStatus.FailedRejectedOptIn -> TODO()
            ENStatus.FailedServiceDisabled -> TODO()
            ENStatus.FailedBluetoothScanningDisabled -> TODO()
            ENStatus.FailedTemporarilyDisabled -> TODO()
            ENStatus.FailedInsufficientStorage -> TODO()
            ENStatus.FailedInternal -> TODO()
        }
    }
}
