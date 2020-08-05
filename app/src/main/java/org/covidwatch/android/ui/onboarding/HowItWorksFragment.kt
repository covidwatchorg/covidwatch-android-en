package org.covidwatch.android.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import org.covidwatch.android.R
import org.covidwatch.android.databinding.FragmentHowItWorksBinding
import org.covidwatch.android.ui.BaseFragment

class HowItWorksFragment : BaseFragment<FragmentHowItWorksBinding>() {

    override fun bind(inflater: LayoutInflater, container: ViewGroup?): FragmentHowItWorksBinding =
        FragmentHowItWorksBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnHowItWorks.setOnClickListener {
            findNavController().navigate(R.id.onboardingFragment)
        }
    }
}