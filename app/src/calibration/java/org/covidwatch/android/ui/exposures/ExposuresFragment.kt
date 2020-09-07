package org.covidwatch.android.ui.exposures

import com.xwray.groupie.ExpandableGroup
import org.covidwatch.android.data.model.CovidExposureInformation

class ExposuresFragment : BaseExposuresFragment() {
    override fun exposuresLoaded(exposures: List<CovidExposureInformation>) {
        if (exposures.isNotEmpty()) adapter.clear()

        exposures.forEach { exposure ->
            adapter.add(
                ExpandableGroup(ExposureItem(exposure)).apply {
                    add(ExposureDetailsItem(exposure))
                }
            )
        }

        if (exposures.isNotEmpty()) adapter.add(FooterItem())
    }
}
