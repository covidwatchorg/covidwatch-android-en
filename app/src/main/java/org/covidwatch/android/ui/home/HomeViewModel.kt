package org.covidwatch.android.ui.home

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.covidwatch.android.R
import org.covidwatch.android.data.FirstTimeUser
import org.covidwatch.android.data.NextStep
import org.covidwatch.android.data.UserFlowRepository
import org.covidwatch.android.data.pref.PreferenceStorage
import org.covidwatch.android.data.risklevel.RiskLevelRepository
import org.covidwatch.android.domain.ProvideDiagnosisKeysUseCase
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.extension.launchUseCase
import org.covidwatch.android.extension.send
import org.covidwatch.android.ui.BaseViewModel
import org.covidwatch.android.ui.event.Event
import org.covidwatch.android.ui.util.DateFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.Calendar.*

class HomeViewModel(
    private val enManager: ExposureNotificationManager,
    private val provideDiagnosisKeysUseCase: ProvideDiagnosisKeysUseCase,
    private val userFlowRepository: UserFlowRepository,
    private val preferences: PreferenceStorage,
    riskLevelRepository: RiskLevelRepository
) : BaseViewModel() {

    private val _showOnboardingAnimation = MutableLiveData<Event<Boolean>>()
    val showOnboardingAnimation: LiveData<Event<Boolean>> = _showOnboardingAnimation

    private val _infoBannerState = MutableLiveData<InfoBannerState>()
    val infoBannerState: LiveData<InfoBannerState> get() = _infoBannerState

    private val _navigateToOnboardingEvent = MutableLiveData<Event<Unit>>()
    val navigateToOnboarding: LiveData<Event<Unit>> get() = _navigateToOnboardingEvent

    val region = preferences.observableRegion

    val riskLevel = riskLevelRepository.riskLevel.asLiveData()

    val nextSteps = riskLevelRepository.riskLevelNextSteps
        .asLiveData()
        .map { it.map(this::replaceDateFlags) }

    /**
     * Searches the input string and replaces the first substring matching this format:
     *          DAYS_FROM_EXPOSURE{LATEST,16,TRUE}
     *
     * With a date relative to significant detected exposures
     * 1st param: either 'EARLIEST' or 'LATEST', describes whether the earliest or latest significant exposure date should be used
     * 2nd param: an integer. The requested date is the exposure date incremented by this many days
     * 3rd param: 'TRUE' or 'FALSE'. True means that the requested date is adjusted to not fall on a weekend (Saturday -> Friday and Sunday -> Monday). False means the requested date is left as-is
     *
     * Currently only replaces the first pattern matched
     */
    private fun replaceDateFlags(step: NextStep): NextStep {
        val pattern = "^.*(DAYS_FROM_EXPOSURE\\{[^}]+\\}).*$".toRegex()
        val description = step.description
        return if (pattern.matches(description)) {
            val flagsMatcher = pattern.find(description)?.groups?.get(1)
            flagsMatcher?.value ?: return step

            val flags = flagsMatcher.value
                .trim()
                .replace("DAYS_FROM_EXPOSURE", "")
                .replace("{", "")
                .replace("}", "")
                .split(",")
                .map { it.trim() }

            step.copy(
                description = description.replaceRange(
                    flagsMatcher.range,
                    nextStepFlagsToText(flags)
                )
            )
        } else step
    }

    /**
     * accepts an array of parsed flags.
     * properly formatted flags will take the values:
     *  flags[0] = {EARLIEST, LATEST}
     *  flags[1] = {any integer}
     *  flags[2] = {TRUE, FALSE}
     */
    private fun nextStepFlagsToText(flags: List<String>): String {
        if (flags.size != 3) return ""

        val exposureDate = when (flags[0]) {
            "LATEST" -> preferences.riskMetrics?.mostRecentSignificantExposureDate
            "EARLIEST" -> preferences.riskMetrics?.leastRecentSignificantExposureDate
            else -> null
        } ?: return ""

        val daysOffset = flags[1].toLongOrNull() ?: return ""

        var requestedDate = Date(
            exposureDate.toInstant().plus(daysOffset, ChronoUnit.DAYS).toEpochMilli()
        )

        if (flags[2] == "TRUE") {
            val calendar = Calendar.getInstance().also { it.time = requestedDate }
            val weekDay = calendar.get(Calendar.DAY_OF_WEEK)

            if (weekDay == SATURDAY) {
                calendar.add(DAY_OF_WEEK, -1)
            } else if (weekDay == SUNDAY) {
                calendar.add(DAY_OF_WEEK, 1)
            }

            requestedDate = calendar.time
        }

        return DateFormatter.format(exposureDate)
    }

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
            launchUseCase(
                provideDiagnosisKeysUseCase,
                ProvideDiagnosisKeysUseCase.Params(recurrent = true)
            )

            _infoBannerState.value = if (enManager.isDisabled()) {
                InfoBannerState.Visible(R.string.turn_on_exposure_notification_text)
            } else {
                InfoBannerState.Hidden
            }
        }
    }
}