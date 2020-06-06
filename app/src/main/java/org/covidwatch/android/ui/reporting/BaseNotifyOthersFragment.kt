package org.covidwatch.android.ui.reporting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
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
                    dialogView.etRiskLevels.setText(riskLevelsAsString(riskLevels))

                    AlertDialog
                        .Builder(context)
                        .setView(dialogView.root)
                        .setPositiveButton(R.string.continue_upload) { _, _ ->
                            // TODO: 03.06.2020 Add validation that check correct format of the string
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