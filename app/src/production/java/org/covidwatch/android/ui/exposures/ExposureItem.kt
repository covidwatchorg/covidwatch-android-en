package org.covidwatch.android.ui.exposures

import android.graphics.drawable.Animatable
import android.view.View
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.viewbinding.BindableItem
import org.covidwatch.android.R
import org.covidwatch.android.data.model.CovidExposureInformation
import org.covidwatch.android.databinding.ItemExposureParentBinding
import org.covidwatch.android.ui.util.DateFormatter

class ExposureItem(val exposure: CovidExposureInformation) :
    BindableItem<ItemExposureParentBinding>() {

    override fun getLayout(): Int = R.layout.item_exposure_parent

    override fun bind(viewBinding: ItemExposureParentBinding, position: Int) {
        viewBinding.text.text = DateFormatter.format(exposure.date)
    }

    override fun initializeViewBinding(view: View) = ItemExposureParentBinding.bind(view)
}