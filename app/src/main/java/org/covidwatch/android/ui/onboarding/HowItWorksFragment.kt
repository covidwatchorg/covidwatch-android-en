package org.covidwatch.android.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import org.covidwatch.android.R
import org.covidwatch.android.databinding.FragmentHowItWorksBinding
import org.covidwatch.android.ui.BaseFragment

class HowItWorksFragment : BaseFragment<FragmentHowItWorksBinding>() {

    override fun bind(inflater: LayoutInflater, container: ViewGroup?): FragmentHowItWorksBinding =
        FragmentHowItWorksBinding.inflate(inflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onBackPressed: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // quit app if user presses back in splash screen
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressed)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnHowItWorks.setOnClickListener {
            findNavController().navigate(R.id.onboardingFragment)
        }
    }
}