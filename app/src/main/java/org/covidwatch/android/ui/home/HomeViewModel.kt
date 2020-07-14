package org.covidwatch.android.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.covidwatch.android.R
import org.covidwatch.android.data.CovidExposureSummary
import org.covidwatch.android.data.FirstTimeUser
import org.covidwatch.android.data.NextStep
import org.covidwatch.android.data.UserFlowRepository
import org.covidwatch.android.data.pref.PreferenceStorage
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.extension.send
import org.covidwatch.android.ui.BaseViewModel
import org.covidwatch.android.ui.event.Event

class HomeViewModel(
    private val enManager: ExposureNotificationManager,
    private val userFlowRepository: UserFlowRepository,
    private val preferences: PreferenceStorage
) : BaseViewModel() {

    private val _showOnboardingAnimation = MutableLiveData<Event<Boolean>>()
    val showOnboardingAnimation: LiveData<Event<Boolean>> = _showOnboardingAnimation

    private val _infoBannerState = MutableLiveData<InfoBannerState>()
    val infoBannerState: LiveData<InfoBannerState> get() = _infoBannerState

    private val _navigateToOnboardingEvent = MutableLiveData<Event<Unit>>()
    val navigateToOnboardingEvent: LiveData<Event<Unit>> get() = _navigateToOnboardingEvent

    val exposureSummary: LiveData<CovidExposureSummary>
        get() = preferences.observableExposureSummary

    val nextSteps: LiveData<List<NextStep>> = preferences.observableRegion.map {
        // TODO: 14.07.2020 Handle different risk levels
        it.nextStepsRiskUnknown
    }

    fun onStart() {
        if (preferences.showOnboardingHomeAnimation) {
            _showOnboardingAnimation.send(true)
            preferences.showOnboardingHomeAnimation = false
        }

        val userFlow = userFlowRepository.getUserFlow()
        if (userFlow is FirstTimeUser) {
            _navigateToOnboardingEvent.value = Event(Unit)
            return
        }

        viewModelScope.launch {
            val enabled = enManager.isEnabled().result() ?: false
            _infoBannerState.value = if (enabled) {
                InfoBannerState.Hidden
            } else {
                InfoBannerState.Visible(R.string.turn_on_exposure_notification_text)
            }
        }
    }
}