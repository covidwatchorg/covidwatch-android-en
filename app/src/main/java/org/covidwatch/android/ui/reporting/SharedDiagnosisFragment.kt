package org.covidwatch.android.ui.reporting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import org.covidwatch.android.R
import org.covidwatch.android.databinding.FragmentThanksForReportingBinding
import org.covidwatch.android.extension.shareApp
import org.covidwatch.android.ui.BaseFragment

class SharedDiagnosisFragment : BaseFragment<FragmentThanksForReportingBinding>() {
    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentThanksForReportingBinding =
        FragmentThanksForReportingBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        with(binding) {
            closeButton.setOnClickListener {
                findNavController().popBackStack(R.id.homeFragment, false)
            }

            shareAppButton.setOnClickListener { context?.shareApp() }

            btnViewPastPositiveDiagnoses.setOnClickListener {
                PositiveDiagnosesFragment().show(childFragmentManager, null)
            }
        }
    }
}
