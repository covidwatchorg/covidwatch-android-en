package org.covidwatch.android.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import org.covidwatch.android.R
import org.covidwatch.android.data.pref.PreferenceStorage
import org.covidwatch.android.databinding.FragmentFinishedOnboardingBinding
import org.covidwatch.android.extension.fromHtml
import org.covidwatch.android.ui.BaseFragment
import org.covidwatch.android.ui.setLogo
import org.koin.android.ext.android.inject

class FinishedOnboardingFragment : BaseFragment<FragmentFinishedOnboardingBinding>() {
    private val prefs: PreferenceStorage by inject()

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFinishedOnboardingBinding =
        FragmentFinishedOnboardingBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val region = prefs.region
        binding.ivLogo.setLogo(region)
        binding.tvRegion.text = getString(R.string.current_region, region.name).fromHtml()
        binding.tvRegion.setOnClickListener { findNavController().navigate(R.id.selectRegionFragment) }

        binding.setupGetStarted.setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }
    }
}