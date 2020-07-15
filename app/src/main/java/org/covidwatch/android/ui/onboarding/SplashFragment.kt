package org.covidwatch.android.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import org.covidwatch.android.databinding.FragmentSplashBinding
import org.covidwatch.android.ui.BaseFragment

class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    override fun bind(inflater: LayoutInflater, container: ViewGroup?): FragmentSplashBinding =
        FragmentSplashBinding.inflate(inflater, container, false)

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

        binding.btnGetStarted.setOnClickListener {
            findNavController().navigate(
                SplashFragmentDirections.actionSplashFragmentToSelectRegionFragment()
            )
        }
    }
}