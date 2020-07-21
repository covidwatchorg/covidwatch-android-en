package org.covidwatch.android.ui.reporting

import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.skydoves.expandablelayout.ExpandableLayout
import org.covidwatch.android.R
import org.covidwatch.android.ui.util.DateFormatter

class PositiveDiagnosisViewHolder(private val root: ExpandableLayout) :
    RecyclerView.ViewHolder(root) {

    private val testStatusImage: ImageView = root.parentLayout.findViewById(R.id.test_status_image)
    private val testStatusText: TextView = root.parentLayout.findViewById(R.id.test_status_text)
    private val testDateText: TextView = root.parentLayout.findViewById(R.id.submit_date)

    private val plum: Int = ContextCompat.getColor(itemView.context, R.color.plum)
    private val tangerine: Int = ContextCompat.getColor(itemView.context, R.color.tangerine)

    fun bind(positiveDiagnosis: PositiveDiagnosisItem) {
        bindTestStatus(positiveDiagnosis.testStatus)
        testDateText.text = root.context.getString(
            R.string.test_date_fmt,
            DateFormatter.format(positiveDiagnosis.testDate)
        )

        root.parentLayout.setOnClickListener {
            if (root.isExpanded) root.collapse() else root.collapse()
        }
    }

    private fun bindTestStatus(testStatus: TestStatus) {
        when (testStatus) {
            TestStatus.Verified -> {
                testStatusImage.setImageResource(R.drawable.ic_check_true)
                testStatusText.setTextColor(plum)
                testStatusText.setText(R.string.verified)
            }
            TestStatus.NeedsVerification -> {
                testStatusImage.setImageResource(R.drawable.ic_info_red)
                testStatusText.setTextColor(tangerine)
                testStatusText.setText(R.string.needs_verification)
            }
        }
    }
}