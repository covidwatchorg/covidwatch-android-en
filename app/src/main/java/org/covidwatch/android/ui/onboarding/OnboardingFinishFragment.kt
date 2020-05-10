package org.covidwatch.android.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import org.covidwatch.android.R
import org.covidwatch.android.databinding.FragmentOnboardingFinishBinding
import org.covidwatch.android.ui.BaseFragment

class OnboardingFinishFragment : BaseFragment<FragmentOnboardingFinishBinding>() {

    override fun binding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOnboardingFinishBinding =
        FragmentOnboardingFinishBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.enableButton.setOnClickListener {
            // TODO: enable exposure-notifications
            findNavController().popBackStack(R.id.homeFragment, false)
        }

        binding.notNowButton.setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }
    }
}