package org.covidwatch.android.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.covidwatch.android.R
import org.covidwatch.android.data.FirstTimeUser
import org.covidwatch.android.data.UserFlowRepository
import org.covidwatch.android.data.pref.PreferenceStorage
import org.covidwatch.android.data.risklevel.RiskLevelRepository
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.extension.send
import org.covidwatch.android.ui.BaseViewModel
import org.covidwatch.android.ui.event.Event

class HomeViewModel(
    private val enManager: ExposureNotificationManager,
    private val userFlowRepository: UserFlowRepository,
    private val riskLevelRepository: RiskLevelRepository,
    private val preferences: PreferenceStorage
) : BaseViewModel() {

    private val _showOnboardingAnimation = MutableLiveData<Event<Boolean>>()
    val showOnboardingAnimation: LiveData<Event<Boolean>> = _showOnboardingAnimation

    private val _infoBannerState = MutableLiveData<InfoBannerState>()
    val infoBannerState: LiveData<InfoBannerState> get() = _infoBannerState

    private val _navigateToOnboardingEvent = MutableLiveData<Event<Unit>>()
    val navigateToOnboarding: LiveData<Event<Unit>> get() = _navigateToOnboardingEvent

    val region = preferences.observableRegion

    val riskLevel = riskLevelRepository.riskLevel.asLiveData()

    val nextSteps = riskLevelRepository.riskLevelNextSteps.asLiveData()

    fun onStart() {
        if (userFlowRepository.getUserFlow() is FirstTimeUser) {
            _navigateToOnboardingEvent.value = Event(Unit)
            return
        }

        if (preferences.showOnboardingHomeAnimation) {
            _showOnboardingAnimation.send(true)
            preferences.showOnboardingHomeAnimation = false
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