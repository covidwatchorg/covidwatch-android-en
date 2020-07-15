package org.covidwatch.android.ui.selectregion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import org.covidwatch.android.data.pref.PreferenceStorage

class SelectRegionViewModel(private val preferences: PreferenceStorage) : ViewModel() {
    fun selectedRegion(position: Int) {
        preferences.selectedRegion = position
    }

    val selectedRegion
        get() = preferences.selectedRegion

    val regions = preferences.observableRegions.map { regions ->
        regions.regions.map { it.name }
    }
}