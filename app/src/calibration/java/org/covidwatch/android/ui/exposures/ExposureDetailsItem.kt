package org.covidwatch.android.ui.exposures

import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import org.covidwatch.android.R
import org.covidwatch.android.data.model.CovidExposureInformation
import org.covidwatch.android.databinding.ItemExposureChildBinding
import org.covidwatch.android.ui.Intents.openBrowser

class ExposureDetailsItem(val exposure: CovidExposureInformation) :
    BindableItem<ItemExposureChildBinding>() {

    override fun getLayout(): Int = R.layout.item_exposure_child

    override fun bind(viewBinding: ItemExposureChildBinding, position: Int) {
        viewBinding.exposure = exposure
        viewBinding.btnLearnMore.setOnClickListener {
            viewBinding.root.context.openBrowser("https://covidwatch.zendesk.com/hc/en-us/articles/360053063574-What-do-the-fields-on-the-Possible-Exposures-page-mean-")
        }
    }

    override fun initializeViewBinding(view: View) = ItemExposureChildBinding.bind(view)
}