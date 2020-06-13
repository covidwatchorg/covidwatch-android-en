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
    fun bind(viewModel: ExposuresViewModel) = ItemBinding.of<Any> { itemBinding, _, item ->
        when (item::class) {
            ExposuresViewModel.Footer::class -> itemBinding.set(
                ItemBinding.VAR_NONE,
                R.layout.exposures_footer
            )
            CovidExposureInformation::class -> itemBinding.set(
                BR.item,
                R.layout.item_exposure
            ).bindExtra(BR.viewModel, viewModel)
        }
    }
}