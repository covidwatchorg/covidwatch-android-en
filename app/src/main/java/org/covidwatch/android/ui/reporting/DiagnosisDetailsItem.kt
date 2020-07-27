package org.covidwatch.android.ui.reporting

import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import org.covidwatch.android.R
import org.covidwatch.android.data.PositiveDiagnosisReport
import org.covidwatch.android.databinding.ItemPositiveDiagnosisChildBinding
import org.covidwatch.android.extension.fromHtml
import org.covidwatch.android.ui.util.DateFormatter

class DiagnosisDetailsItem(
    val viewModel: PositiveDiagnosesViewModel,
    val diagnosis: PositiveDiagnosisReport
) :
    BindableItem<ItemPositiveDiagnosisChildBinding>() {

    override fun getLayout(): Int = R.layout.item_positive_diagnosis_child

    override fun bind(viewBinding: ItemPositiveDiagnosisChildBinding, position: Int) {
        val symptomsStartDate = diagnosis.verificationData?.symptomsStartDate
        val context = viewBinding.root.context

        val symptomsDateText = symptomsStartDate?.let { DateFormatter.format(it) }
            ?: context.getString(R.string.no_symptoms_report)
        viewBinding.symptomsDate.text = context.getString(
            R.string.symptoms_diagnosis_date,
            symptomsDateText
        ).fromHtml()

        val possibleInfectionDate = diagnosis.verificationData?.possibleInfectionDate
        val infectionDateText = possibleInfectionDate?.let { DateFormatter.format(it) }
            ?: context.getString(R.string.no_infection_date_report)
        viewBinding.infectionDate.text =
            context.getString(R.string.infection_diagnosis_date, infectionDateText).fromHtml()

        val testDiagnosisDate = diagnosis.verificationData?.testDate
        val testDateText = testDiagnosisDate?.let { DateFormatter.format(it) }
            ?: context.getString(R.string.no_test_date_report)
        viewBinding.testDate.text =
            context.getString(R.string.test_diagnosis_date, testDateText).fromHtml()

        viewBinding.btnDeleteDiagnosis.setOnClickListener {
            viewModel.deleteDiagnosis(diagnosis)
        }
    }

    override fun initializeViewBinding(view: View) = ItemPositiveDiagnosisChildBinding.bind(view)
}