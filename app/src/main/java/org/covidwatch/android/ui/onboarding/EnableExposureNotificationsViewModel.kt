package org.covidwatch.android.ui.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.ui.event.Event

class EnableExposureNotificationsViewModel(
    private val exposureNotificationManager: ExposureNotificationManager
) : ViewModel() {

    private val _exposureNotificationResult = MutableLiveData<Event<Unit>>()
    val exposureNotificationResult: LiveData<Event<Unit>> = _exposureNotificationResult

    fun onEnableClicked() {
        viewModelScope.launch {
            exposureNotificationManager.start().apply {
                success {
                    _exposureNotificationResult.value = Event(Unit)
                }
                failure(::handleError)
            }
        }
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