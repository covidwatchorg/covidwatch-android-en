package org.covidwatch.android.ui.reporting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import org.covidwatch.android.R
import org.covidwatch.android.databinding.DialogPastPositiveDiagnosesBinding
import org.covidwatch.android.databinding.FragmentThanksForReportingBinding
import org.covidwatch.android.extension.observe
import org.covidwatch.android.extension.shareApp
import org.covidwatch.android.ui.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SharedDiagnosisFragment : BaseFragment<FragmentThanksForReportingBinding>() {
    private val adapter = PositiveDiagnosisAdapter()
    private val viewModel: SharedDiagnosisViewModel by viewModel()

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentThanksForReportingBinding =
        FragmentThanksForReportingBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewModel) {
            observe(positiveDiagnosis) { adapter.setItems(it) }
        }

        with(binding) {
            closeButton.setOnClickListener {
                findNavController().popBackStack(R.id.homeFragment, false)
            }
            shareAppButton.setOnClickListener { context?.shareApp() }

            btnViewPastPositiveDiagnoses.setOnClickListener {
                if (adapter.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        R.string.no_past_positive_diagnoses,
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                val dialogView =
                    DialogPastPositiveDiagnosesBinding.inflate(LayoutInflater.from(context))
                dialogView.pastPositiveDiagnosesList.addItemDecoration(dividerItemDecoration())
                dialogView.pastPositiveDiagnosesList.adapter = adapter
                val dialog = AlertDialog
                    .Builder(requireContext())
                    .setView(dialogView.root)
                    .create()
                dialogView.closeButton.setOnClickListener { dialog.dismiss() }
                dialog.show()
            }
        }
    }

    private fun dividerItemDecoration(): RecyclerView.ItemDecoration {
        return DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
    }
}
