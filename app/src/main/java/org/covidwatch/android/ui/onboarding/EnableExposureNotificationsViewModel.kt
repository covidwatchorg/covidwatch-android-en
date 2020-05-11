package org.covidwatch.android.ui.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.functional.Either
import org.covidwatch.android.ui.event.Event

class EnableExposureNotificationsViewModel(
    private val exposureNotificationManager: ExposureNotificationManager
) : ViewModel() {

    private val _exposureNotificationResult = MutableLiveData<Event<Either<ENStatus, Void>>>()
    val exposureNotificationResult: LiveData<Event<Either<ENStatus, Void>>> = _exposureNotificationResult

    fun onEnableClicked() {
        viewModelScope.launch {
            _exposureNotificationResult.value = Event(exposureNotificationManager.start())
        }
    }
}