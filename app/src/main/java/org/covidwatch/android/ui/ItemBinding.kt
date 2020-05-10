package org.covidwatch.android.ui

import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.covidwatch.android.BR
import org.covidwatch.android.R
import org.covidwatch.android.data.CovidExposureInformation
import org.covidwatch.android.ui.exposures.ExposuresViewModel

object ItemBindings {

    @JvmStatic
    fun bind(): ItemBinding<CovidExposureInformation> =
        ItemBinding.of(
            BR.item,
            R.layout.item_exposure_info
        )

    @JvmStatic
    fun bind(viewModel: ExposuresViewModel): ItemBinding<CovidExposureInformation> =
        ItemBinding.of<CovidExposureInformation>(
            BR.item,
            R.layout.item_exposure
        ).bindExtra(BR.viewModel, viewModel)
}