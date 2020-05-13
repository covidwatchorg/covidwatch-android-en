package org.covidwatch.android.ui.reporting

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.covidwatch.android.R

class PositiveDiagnosisViewHolder(root: View) : RecyclerView.ViewHolder(root) {

    private val testStatusImage: ImageView = root.findViewById(R.id.test_status_image)
    private val testStatusText: TextView = root.findViewById(R.id.test_status_text)
    private val testDateText: TextView = root.findViewById(R.id.test_date)

    private val plum: Int = ContextCompat.getColor(itemView.context, R.color.plum)
    private val tangerine: Int = ContextCompat.getColor(itemView.context, R.color.tangerine)

    fun bind(positiveDiagnosis: PositiveDiagnosisItem) {
        bindTestStatus(positiveDiagnosis.testStatus)
        testDateText.text = positiveDiagnosis.testDate
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