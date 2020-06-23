package org.covidwatch.android.ui.reporting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import org.covidwatch.android.R
import org.covidwatch.android.databinding.DialogPastPositiveDiagnosesBinding
import org.covidwatch.android.databinding.FragmentVerifyPositiveDiagnosisBinding
import org.covidwatch.android.ui.BaseFragment

class VerifyPositiveDiagnosisFragment : BaseFragment<FragmentVerifyPositiveDiagnosisBinding>() {

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentVerifyPositiveDiagnosisBinding =
        FragmentVerifyPositiveDiagnosisBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            closeButton.setOnClickListener { findNavController().popBackStack() }

            btnFinishVerification.setOnClickListener {
                findNavController().navigate(R.id.thanksForReportingFragment)
            }

            etTestedDate.setOnClickListener {
                val dialogView =
                    DialogPastPositiveDiagnosesBinding.inflate(LayoutInflater.from(context))
                val dialog = AlertDialog
                    .Builder(requireContext())
                    .setView(dialogView.root)
                    .create()
                dialog.show()
            }
        }
    }
}