package org.covidwatch.android.ui.selectregion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import org.covidwatch.android.data.model.Region
import org.covidwatch.android.data.pref.PreferenceStorage
import org.covidwatch.android.extension.send
import org.covidwatch.android.ui.event.Event
import org.covidwatch.android.ui.util.combineAndCompute

class SelectRegionViewModel(
    private val preferences: PreferenceStorage
) : ViewModel() {
    private var onboarding: Boolean = false

    private val _closeScreen = MutableLiveData<Event<Unit>>()
    val closeScreen: LiveData<Event<Unit>> = _closeScreen

    private val _showSetupCompleteScreen = MutableLiveData<Event<Unit>>()
    val showSetupCompleteScreen: LiveData<Event<Unit>> = _showSetupCompleteScreen

    val selectedRegion
        get() = preferences.observableRegions.combineAndCompute(preferences.observableRegion) { regions, region ->
            regions.regions.indexOf(region)
        }

    private val _regions = mutableListOf<Region>()
    val regions = preferences.observableRegions.map { regions ->
        _regions.clear()
        _regions.addAll(regions.regions)

        regions.regions.map { it.name }
    }

    fun continueClicked() {
        if (onboarding) _showSetupCompleteScreen.send()
        else _closeScreen.send()
    }

    fun selectedRegion(position: Int) {
        preferences.selectedRegion = _regions[position].id
    }

    fun setOnboarding(onboarding: Boolean) {
        this.onboarding = onboarding
    }
}