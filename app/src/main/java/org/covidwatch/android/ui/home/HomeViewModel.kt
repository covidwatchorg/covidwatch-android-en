package org.covidwatch.android.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.covidwatch.android.R
import org.covidwatch.android.data.CovidExposureSummary
import org.covidwatch.android.data.FirstTimeUser
import org.covidwatch.android.data.UserFlowRepository
import org.covidwatch.android.data.pref.PreferenceStorage
import org.covidwatch.android.domain.ProvideDiagnosisKeysUseCase
import org.covidwatch.android.domain.TestedRepository
import org.covidwatch.android.extension.launchUseCase
import org.covidwatch.android.ui.event.Event
import org.koin.java.KoinJavaComponent.inject

class HomeViewModel(
    private val userFlowRepository: UserFlowRepository,
    private val testedRepository: TestedRepository,
    private val preferenceStorage: PreferenceStorage
) : ViewModel() {
    private val provideDiagnosisKeysUseCase: ProvideDiagnosisKeysUseCase by inject(
        ProvideDiagnosisKeysUseCase::class.java
    )

    private val _isUserTestedPositive = MutableLiveData<Boolean>()
    val isUserTestedPositive: LiveData<Boolean> get() = _isUserTestedPositive

    private val _infoBannerState = MutableLiveData<InfoBannerState>()
    val infoBannerState: LiveData<InfoBannerState> get() = _infoBannerState

    private val _warningBannerState = MutableLiveData<WarningBannerState>()
    val warningBannerState: LiveData<WarningBannerState> get() = _warningBannerState

    private val _navigateToOnboardingEvent = MutableLiveData<Event<Unit>>()
    val navigateToOnboardingEvent: LiveData<Event<Unit>> get() = _navigateToOnboardingEvent

    val exposureSummary: LiveData<CovidExposureSummary>
        get() = preferenceStorage.observableExposureSummary

    fun onStart() {
        val userFlow = userFlowRepository.getUserFlow()
        if (userFlow is FirstTimeUser) {
            _navigateToOnboardingEvent.value = Event(Unit)
            return
        }

        checkIfUserTestedPositive()

//        viewModelScope.launchUseCase(provideDiagnosisKeysUseCase)
    }

    private fun checkIfUserTestedPositive() {
        val isUserTestedPositive = testedRepository.isUserTestedPositive()
        _isUserTestedPositive.value = isUserTestedPositive
        if (isUserTestedPositive) {
            _warningBannerState.value = WarningBannerState.Visible(R.string.reported_alert_text)
        }
    }
}