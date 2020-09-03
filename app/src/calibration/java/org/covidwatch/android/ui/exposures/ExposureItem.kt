package org.covidwatch.android.ui.exposures

import android.graphics.drawable.Animatable
import android.view.View
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.viewbinding.BindableItem
import org.covidwatch.android.R
import org.covidwatch.android.data.CovidExposureInformation
import org.covidwatch.android.databinding.ItemExposureParentBinding
import org.covidwatch.android.extension.setRippleBackground
import org.covidwatch.android.ui.util.DateFormatter

class ExposureItem(val exposure: CovidExposureInformation) :
    BindableItem<ItemExposureParentBinding>(), ExpandableItem {
    private var expandableGroup: ExpandableGroup? = null

    override fun setExpandableGroup(expandableGroup: ExpandableGroup) {
        this.expandableGroup = expandableGroup
    }

    override fun getLayout(): Int = R.layout.item_exposure_parent

    override fun bind(viewBinding: ItemExposureParentBinding, position: Int) {
        viewBinding.text.text = DateFormatter.format(exposure.date)

        viewBinding.text.setRippleBackground()
        viewBinding.text.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0,
            0,
            if (expandableGroup!!.isExpanded) R.drawable.collapse else R.drawable.expand,
            0
        )
        viewBinding.root.setOnClickListener {
            expandableGroup!!.onToggleExpanded()
            bindIcon(viewBinding)
        }
    }

    private fun bindIcon(viewBinding: ItemExposureParentBinding) {
        viewBinding.text.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0,
            0,
            if (expandableGroup!!.isExpanded) R.drawable.collapse_animated else R.drawable.expand_animated,
            0
        )
        val drawable = viewBinding.text.compoundDrawables[2] as Animatable
        drawable.start()
    }

    override fun initializeViewBinding(view: View) = ItemExposureParentBinding.bind(view)

}