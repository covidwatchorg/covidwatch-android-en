package org.covidwatch.android.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.covidwatch.android.databinding.FragmentOnboardingPageBinding
import org.covidwatch.android.ui.BaseFragment

internal const val ONBOARDING_PAGE_PARAMETERS_KEY = "ONBOARDING_PAGE_PARAMETERS_KEY"

class OnboardingPageFragment : BaseFragment<FragmentOnboardingPageBinding>() {

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOnboardingPageBinding =
        FragmentOnboardingPageBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parameters: OnboardingPageParameters =
            requireNotNull(arguments?.getParcelable(ONBOARDING_PAGE_PARAMETERS_KEY))

        binding.title.setText(parameters.title)
        binding.image.setImageResource(parameters.image)
        binding.subtitle.setText(parameters.subtitle)
    }
}