package org.covidwatch.android.data

import org.covidwatch.android.data.pref.PreferenceStorage


class UserFlowRepository(
    private val prefs: PreferenceStorage
) {
    fun getUserFlow() = when {
        !prefs.onboardingFinished -> FirstTimeUser
        else -> ReturnUser
    }

    fun finishOnboarding() {
        prefs.onboardingFinished = true
    }
}