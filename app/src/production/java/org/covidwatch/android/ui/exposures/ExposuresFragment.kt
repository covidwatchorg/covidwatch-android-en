package org.covidwatch.android.ui.exposures

import com.xwray.groupie.ExpandableGroup
import org.covidwatch.android.data.CovidExposureInformation

class ExposuresFragment : BaseExposuresFragment() {
    override fun exposuresLoaded(exposures: List<CovidExposureInformation>) {
        if (exposures.isNotEmpty()) adapter.clear()

        exposures.forEach { exposure -> adapter.add(ExposureItem(exposure)) }

        if (exposures.isNotEmpty()) adapter.add(FooterItem())
    }
}
