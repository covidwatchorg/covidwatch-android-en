package org.covidwatch.android.data.pref

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.android.gms.nearby.exposurenotification.ExposureConfiguration
import com.google.android.gms.nearby.exposurenotification.ExposureSummary
import org.covidwatch.android.data.CovidExposureSummary
import org.covidwatch.android.data.Region
import org.covidwatch.android.data.Regions
import org.covidwatch.android.exposurenotification.RandomEnObjects

@Suppress("UNUSED_PARAMETER", "unused")
class FakePreferenceStorage : PreferenceStorage {
    override var lastFetchDate: Long
        get() = 0
        set(value) {}
    override var onboardingFinished: Boolean
        get() = true
        set(value) {}

    override var showOnboardingHomeAnimation: Boolean
        get() = true
        set(value) {}
    override var exposureSummary: CovidExposureSummary
        get() = RandomEnObjects.exposureSummary.asCovidExposureSummary()
        set(value) {}

    private fun ExposureSummary.asCovidExposureSummary() = CovidExposureSummary(
        daysSinceLastExposure,
        matchedKeyCount,
        (maximumRiskScore * 8.0 / 4096).toInt(),
        attenuationDurationsInMinutes,
        (summationRiskScore * 8.0 / 4096).toInt()
    )

    override fun resetExposureSummary() {}
    override var regions: Regions
        get() = TODO("not implemented")
        set(value) {}
    override val observableRegions: LiveData<Regions>
        get() = TODO("not implemented")
    override val region: Region
        get() = TODO("not implemented")
    override var selectedRegion: Int
        get() = TODO("not implemented")
        set(value) {}
    override val observableRegion: LiveData<Region>
        get() = TODO("not implemented")

    override var exposureConfiguration: ExposureConfiguration
        get() = ExposureConfiguration.ExposureConfigurationBuilder()
            .setMinimumRiskScore(1)
            .setDurationAtAttenuationThresholds(58, 73)
            .setAttenuationScores(2, 5, 8, 8, 8, 8, 8, 8)
            .setDaysSinceLastExposureScores(1, 2, 2, 4, 6, 8, 8, 8)
            .setDurationScores(1, 1, 4, 7, 7, 8, 8, 8)
            .setTransmissionRiskScores(0, 3, 6, 8, 8, 6, 0, 6)
            .build()
        set(value) {}
    override val observableExposureSummary: LiveData<CovidExposureSummary>
        get() = liveData { emit(exposureSummary) }
}