package org.covidwatch.android.ui.reporting

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import org.covidwatch.android.R
import org.covidwatch.android.databinding.DialogRiskLevelsBinding
import org.covidwatch.android.databinding.FragmentNotifyOthersBinding
import org.covidwatch.android.extension.observe
import org.covidwatch.android.extension.observeEvent
import org.covidwatch.android.ui.BaseViewModelFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


open class BaseNotifyOthersFragment :
    BaseViewModelFragment<FragmentNotifyOthersBinding, NotifyOthersViewModel>() {

    override val viewModel: NotifyOthersViewModel by viewModel()

    private val adapter = PositiveDiagnosisAdapter()

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNotifyOthersBinding =
        FragmentNotifyOthersBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            closeButton.setOnClickListener {
                findNavController().popBackStack()
            }
            sharePositiveDiagnosisButton.setOnClickListener {
                viewModel.sharePositiveDiagnosis()
            }

            pastPositiveDiagnosesList.addItemDecoration(dividerItemDecoration())
            pastPositiveDiagnosesList.adapter = adapter
        }

        with(viewModel) {
            observe(positiveDiagnosis) { adapter.setItems(it) }
            observeEvent(setTransmissionLevelRisk) { riskLevels ->
                context?.let { context ->
                    val dialogView = DialogRiskLevelsBinding.inflate(
                        LayoutInflater.from(context)
                    )
                    val keysNumber = riskLevels.size

                    dialogView.title.text =
                        getString(R.string.transmission_risk_dialog_title, keysNumber)

                    dialogView.description.text =
                        HtmlCompat.fromHtml(
                            getString(R.string.transmission_risk_dialog_message, keysNumber),
                            HtmlCompat.FROM_HTML_MODE_COMPACT
                        )

                    dialogView.etRiskLevels.filters =
                        arrayOf(InputFilter.LengthFilter(keysNumber * 2))
                    dialogView.etRiskLevels.setText(riskLevelsAsString(riskLevels))

                    AlertDialog
                        .Builder(context)
                        .setView(dialogView.root)
                        .setPositiveButton(R.string.continue_upload) { _, _ ->
                            val risksLevels = dialogView.etRiskLevels.text.toString()
                            viewModel.shareReport(risksLevels)
                        }
                        .setNegativeButton(R.string.cancel, null)
                        .create()
                        .show()
                }
            }
        }
    }

    private fun dividerItemDecoration(): RecyclerView.ItemDecoration {
        return DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
    }
}