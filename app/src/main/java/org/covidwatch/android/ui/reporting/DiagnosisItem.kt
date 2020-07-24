package org.covidwatch.android.ui.reporting

import android.content.Context
import android.graphics.drawable.Animatable
import android.view.View
import androidx.core.content.ContextCompat
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.viewbinding.BindableItem
import org.covidwatch.android.R
import org.covidwatch.android.data.PositiveDiagnosisReport
import org.covidwatch.android.databinding.ItemPositiveDiagnosisParentBinding
import org.covidwatch.android.extension.fromHtml
import org.covidwatch.android.ui.util.DateFormatter

class DiagnosisItem(val context: Context, val diagnosis: PositiveDiagnosisReport) :
    BindableItem<ItemPositiveDiagnosisParentBinding>(), ExpandableItem {
    private var expandableGroup: ExpandableGroup? = null

    private var plum: Int = ContextCompat.getColor(context, R.color.plum)
    private var tangerine: Int = ContextCompat.getColor(context, R.color.tangerine)

    override fun setExpandableGroup(expandableGroup: ExpandableGroup) {
        this.expandableGroup = expandableGroup
    }

    override fun getLayout(): Int = R.layout.item_positive_diagnosis_parent

    override fun bind(viewBinding: ItemPositiveDiagnosisParentBinding, position: Int) {
        viewBinding.expandArrow.setImageResource(if (expandableGroup!!.isExpanded) R.drawable.collapse else R.drawable.expand)
        viewBinding.root.setOnClickListener {
            expandableGroup!!.onToggleExpanded()
            bindIcon(viewBinding)
        }

        bindTestStatus(viewBinding, diagnosis.verified)

        viewBinding.submitDate.text = context.getString(
            R.string.submitted_diagnosis_date,
            DateFormatter.format(diagnosis.reportDate)
        ).fromHtml()
    }

    private fun bindTestStatus(viewBinding: ItemPositiveDiagnosisParentBinding, verified: Boolean) {
        if (verified) {
            viewBinding.testStatusImage.setImageResource(R.drawable.ic_check_true)
            viewBinding.testStatusText.setTextColor(plum)
            viewBinding.testStatusText.setText(R.string.verified)
        } else {
            viewBinding.testStatusImage.setImageResource(R.drawable.ic_info_red)
            viewBinding.testStatusText.setTextColor(tangerine)
            viewBinding.testStatusText.setText(R.string.needs_verification)
        }
    }

    private fun bindIcon(viewBinding: ItemPositiveDiagnosisParentBinding) {
        viewBinding.expandArrow.setImageResource(if (expandableGroup!!.isExpanded) R.drawable.collapse_animated else R.drawable.expand_animated)
        val drawable = viewBinding.expandArrow.drawable as Animatable
        drawable.start()
    }

    override fun initializeViewBinding(view: View) = ItemPositiveDiagnosisParentBinding.bind(view)
}