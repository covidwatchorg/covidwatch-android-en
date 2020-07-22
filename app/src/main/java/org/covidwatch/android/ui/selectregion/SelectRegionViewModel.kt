package org.covidwatch.android.ui.selectregion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import org.covidwatch.android.data.pref.PreferenceStorage
import org.covidwatch.android.extension.send
import org.covidwatch.android.ui.event.Event

class SelectRegionViewModel(private val preferences: PreferenceStorage) : ViewModel() {
    private var onboarding: Boolean = false

    private val _closeScreen = MutableLiveData<Event<Unit>>()
    val closeScreen: LiveData<Event<Unit>> = _closeScreen

    private val _showRegionPreviewScreen = MutableLiveData<Event<Unit>>()
    val showRegionPreviewScreen: LiveData<Event<Unit>> = _showRegionPreviewScreen

    fun continueClicked() {
        if (onboarding) _showRegionPreviewScreen.send()
        else _closeScreen.send()
    }

    fun selectedRegion(position: Int) {
        preferences.selectedRegion = position
    }

    fun setOnboarding(boolean: Boolean?) {
        this.onboarding = boolean ?: false
    }

    val selectedRegion
        get() = preferences.selectedRegion

    val regions = preferences.observableRegions.map { regions ->
        regions.regions.map { it.name }
    }
}