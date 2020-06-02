package org.covidwatch.android.ui.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.ui.BaseViewModel
import org.covidwatch.android.ui.event.Event

class EnableExposureNotificationsViewModel(
    private val enManager: ExposureNotificationManager
) : BaseViewModel() {

    private val _exposureNotificationResult = MutableLiveData<Event<Unit>>()
    val exposureNotificationResult: LiveData<Event<Unit>> = _exposureNotificationResult

    fun onEnableClicked() {
        viewModelScope.launch {
            withPermission(ExposureNotificationManager.PERMISSION_START_REQUEST_CODE) {
                enManager.start().apply {
                    success { _exposureNotificationResult.value = Event(Unit) }
                    failure { handleStatus(it) }
                }
            }
        }
    }
}