package org.covidwatch.android.ui.reporting

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.skydoves.expandablelayout.ExpandableLayout
import org.covidwatch.android.R
import org.covidwatch.android.data.PositiveDiagnosisReport
import org.covidwatch.android.extension.fromHtml
import org.covidwatch.android.ui.util.DateFormatter

class PositiveDiagnosisViewHolder(private val root: ExpandableLayout) :
    RecyclerView.ViewHolder(root) {

    private val testStatusImage: ImageView = root.parentLayout.findViewById(R.id.test_status_image)
    private val testStatusText: TextView = root.parentLayout.findViewById(R.id.test_status_text)
    private val submittedDate: TextView = root.parentLayout.findViewById(R.id.submit_date)

    private val symptomsDate: TextView = root.secondLayout.findViewById(R.id.symptoms_date)
    private val infectionDate: TextView = root.secondLayout.findViewById(R.id.infection_date)
    private val testDate: TextView = root.secondLayout.findViewById(R.id.test_date)

    private val btnDeleteDiagnosis: TextView =
        root.secondLayout.findViewById(R.id.btn_delete_diagnosis)

    private val plum: Int = ContextCompat.getColor(itemView.context, R.color.plum)
    private val tangerine: Int = ContextCompat.getColor(itemView.context, R.color.tangerine)

    private val context: Context
        get() = root.context

    fun bind(
        positiveDiagnosis: PositiveDiagnosisReport,
        viewModel: PositiveDiagnosesViewModel
    ) {
        bindTestStatus(positiveDiagnosis.verified)
        submittedDate.text = context.getString(
            R.string.submitted_diagnosis_date,
            DateFormatter.format(positiveDiagnosis.reportDate)
        ).fromHtml()

        root.parentLayout.setOnClickListener {
            if (root.isExpanded) root.collapse() else root.expand()
        }

        val symptomsStartDate = positiveDiagnosis.verificationData?.symptomsStartDate
        val symptomsDateText = symptomsStartDate?.let { DateFormatter.format(it) }
            ?: context.getString(R.string.no_symptoms_report)
        symptomsDate.text = context.getString(
            R.string.symptoms_diagnosis_date,
            symptomsDateText
        )

        val possibleInfectionDate = positiveDiagnosis.verificationData?.possibleInfectionDate
        val infectionDateText = possibleInfectionDate?.let { DateFormatter.format(it) }
            ?: context.getString(R.string.no_infection_date_report)
        infectionDate.text = context.getString(R.string.infection_diagnosis_date, infectionDateText)

        val testDiagnosisDate = positiveDiagnosis.verificationData?.testDate
        val testDateText = testDiagnosisDate?.let { DateFormatter.format(it) }
            ?: context.getString(R.string.no_test_date_report)
        testDate.text = context.getString(R.string.test_diagnosis_date, testDateText)

        btnDeleteDiagnosis.setOnClickListener {
            viewModel.deleteDiagnosis(positiveDiagnosis)
        }
    }

    private fun bindTestStatus(verified: Boolean) {
        if (verified) {
            testStatusImage.setImageResource(R.drawable.ic_check_true)
            testStatusText.setTextColor(plum)
            testStatusText.setText(R.string.verified)
        } else {
            testStatusImage.setImageResource(R.drawable.ic_info_red)
            testStatusText.setTextColor(tangerine)
            testStatusText.setText(R.string.needs_verification)
        }
    }
}