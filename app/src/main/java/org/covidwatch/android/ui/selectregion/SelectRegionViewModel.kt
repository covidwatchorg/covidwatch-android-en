package org.covidwatch.android.ui.selectregion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import org.covidwatch.android.R
import org.covidwatch.android.data.pref.PreferenceStorage
import org.covidwatch.android.extension.send
import org.covidwatch.android.ui.IResourcesProvider
import org.covidwatch.android.ui.event.Event

class SelectRegionViewModel(
    private val preferences: PreferenceStorage,
    private val resources: IResourcesProvider
) : ViewModel() {
    private var onboarding: Boolean = false

    private val _closeScreen = MutableLiveData<Event<Unit>>()
    val closeScreen: LiveData<Event<Unit>> = _closeScreen

    private val _showSetupCompleteScreen = MutableLiveData<Event<Unit>>()
    val showSetupCompleteScreen: LiveData<Event<Unit>> = _showSetupCompleteScreen

    private val _showContinueButton = MutableLiveData<Boolean>()
    val showContinueButton: LiveData<Boolean> = _showContinueButton

    fun continueClicked() {
        if (onboarding) _showSetupCompleteScreen.send()
        else _closeScreen.send()
    }

    fun selectedRegion(position: Int) {
        if (onboarding) {
            // Show continue if not first region selected which is a dummy "select region" item
            _showContinueButton.value = position != 0
            if (position > 0) preferences.selectedRegion = position - 1
        } else {
            preferences.selectedRegion = position
        }
    }

    fun setOnboarding(boolean: Boolean?) {
        this.onboarding = boolean ?: false
    }

    val selectedRegion
        get() = preferences.selectedRegion

    val regions = preferences.observableRegions.map { regions ->
        if (onboarding) {
            mutableListOf<String>().apply {
                add(resources.getString(R.string.select_region_label))
                addAll(regions.regions.map { it.name })
            }
        } else {
            regions.regions.map { it.name }
        }
    }
}