package org.covidwatch.android.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import org.covidwatch.android.R
import org.covidwatch.android.databinding.FragmentSplashBinding
import org.covidwatch.android.ui.BaseFragment

class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    override fun bind(inflater: LayoutInflater, container: ViewGroup?): FragmentSplashBinding =
        FragmentSplashBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGetStarted.setOnClickListener {
            findNavController().popBackStack()
            findNavController().navigate(R.id.howItWorksFragment)
        }
    }
}