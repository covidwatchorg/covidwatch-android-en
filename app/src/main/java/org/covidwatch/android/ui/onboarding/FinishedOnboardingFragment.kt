package org.covidwatch.android.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import org.covidwatch.android.R
import org.covidwatch.android.databinding.FragmentFinishedOnboardingBinding
import org.covidwatch.android.extension.shareApp
import org.covidwatch.android.ui.BaseFragment

class FinishedOnboardingFragment : BaseFragment<FragmentFinishedOnboardingBinding>() {

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFinishedOnboardingBinding =
        FragmentFinishedOnboardingBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.shareAppButton.setOnClickListener {
            context?.shareApp()
        }

        binding.seeExposureSummary.setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }
    }
}