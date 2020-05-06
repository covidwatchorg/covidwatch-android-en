package org.covidwatch.android.data

import org.covidwatch.android.data.pref.PreferenceStorage


class UserFlowRepository(
    private val prefs: PreferenceStorage
) {
    fun getUserFlow() = when {
        !prefs.onboardingFinished -> Setup
        prefs.firstLaunch -> FirstTimeUser
        else -> ReturnUser
    }

    fun markFirstLaunch() {
        prefs.firstLaunch = false
    }

    fun finishOnboarding() {
        prefs.onboardingFinished = true
    }
}