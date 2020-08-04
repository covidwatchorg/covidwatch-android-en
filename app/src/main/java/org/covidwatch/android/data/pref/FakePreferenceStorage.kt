package org.covidwatch.android.data.pref

import androidx.lifecycle.LiveData
import org.covidwatch.android.data.*
import java.time.Instant

@Suppress("UNUSED_PARAMETER", "unused")
class FakePreferenceStorage(
    override var lastCheckedForExposures: Instant?,
    override val observableLastCheckedForExposures: LiveData<Instant>,
    override val version: Int
) : PreferenceStorage {
    override var lastFetchDate: Long
        get() = 0
        set(value) {}
    override var onboardingFinished: Boolean
        get() = true
        set(value) {}

    override var showOnboardingHomeAnimation: Boolean
        get() = true
        set(value) {}

    override var riskMetrics: RiskMetrics? = null

    override val observableRiskMetrics: LiveData<RiskMetrics?>
        get() = TODO("not implemented")

    override var regions: Regions
        get() = TODO("not implemented")
        set(value) {}
    override val observableRegions: LiveData<Regions>
        get() = TODO("not implemented")
    override val region: Region
        get() = TODO("not implemented")
    override val riskModelConfiguration: RiskModelConfiguration
        get() = ArizonaRiskModelConfiguration()
    override var selectedRegion: Int
        get() = TODO("not implemented")
        set(value) {}
    override val observableRegion: LiveData<Region>
        get() = TODO("not implemented")

    override val exposureConfiguration: CovidExposureConfiguration
        get() = TODO("not implemented")
}