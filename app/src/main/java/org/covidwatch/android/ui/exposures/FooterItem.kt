package org.covidwatch.android.ui.exposures

import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import org.covidwatch.android.R
import org.covidwatch.android.databinding.ExposuresFooterBinding

class FooterItem : BindableItem<ExposuresFooterBinding>() {

    override fun getLayout(): Int = R.layout.exposures_footer

    override fun bind(viewBinding: ExposuresFooterBinding, position: Int) = Unit

    override fun initializeViewBinding(view: View) = ExposuresFooterBinding.bind(view)
}