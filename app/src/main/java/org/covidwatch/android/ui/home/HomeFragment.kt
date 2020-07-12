package org.covidwatch.android.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.covidwatch.android.R
import org.covidwatch.android.data.CovidExposureSummary
import org.covidwatch.android.data.RiskScoreLevel.*
import org.covidwatch.android.data.level
import org.covidwatch.android.databinding.FragmentHomeBinding
import org.covidwatch.android.extension.shareApp
import org.covidwatch.android.ui.BaseFragment
import org.covidwatch.android.ui.event.EventObserver
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val homeViewModel: HomeViewModel by viewModel()

    override fun bind(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding =
        FragmentHomeBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.onStart()
        homeViewModel.navigateToOnboardingEvent.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.splashFragment)
        })

        homeViewModel.exposureSummary.observe(viewLifecycleOwner, Observer(::bindExposureSummary))

        homeViewModel.infoBannerState.observe(viewLifecycleOwner, Observer { banner ->
            when (banner) {
                is InfoBannerState.Visible -> {
                    binding.infoBanner.isVisible = true
                    binding.infoBanner.setText(banner.text)
                }
                InfoBannerState.Hidden -> {
                    binding.infoBanner.isVisible = false
                }
            }
        })

        // TODO: 12.07.2020 Change to a value from a server
        binding.tvRegion.text = HtmlCompat.fromHtml(
            getString(R.string.current_region, "Arizona"),
            HtmlCompat.FROM_HTML_MODE_COMPACT
        )
        lifecycleScope.launch {
            // TODO: 12.07.2020 Change time of the animation
            delay(1000)
            binding.homeScreenArt.isVisible = true
        }
        initClickListeners()
    }

    private fun initClickListeners() {
        with(binding) {
            notifyOthersButton.setOnClickListener {
                findNavController().navigate(R.id.notifyOthersFragment)
            }
            menu.setOnClickListener {
                findNavController().navigate(R.id.menuFragment)
            }
            shareAppButton.setOnClickListener {
                context?.shareApp()
            }
            infoBanner.setOnClickListener {
                findNavController().navigate(R.id.enableExposureNotificationsFragment)
            }

            exposureSummary.root.setOnClickListener {
                findNavController().navigate(R.id.exposuresFragment)
            }
        }
    }

    private fun bindExposureSummary(exposureSummary: CovidExposureSummary) {
        with(binding.exposureSummary) {
            val days = exposureSummary.daySinceLastExposure.takeIf { it > 0 }?.toString()
            val total = exposureSummary.matchedKeyCount.takeIf { it > 0 }?.toString()
            val risk = exposureSummary.maximumRiskScore.takeIf { it > 0 }?.toString()
            when (exposureSummary.maximumRiskScore.level) {
                HIGH -> highRiskScore.background =
                    context?.getDrawable(R.drawable.bg_exposure_dashboard_high_risk)
                MEDIUM -> highRiskScore.background =
                    context?.getDrawable(R.drawable.bg_exposure_dashboard_med_risk)
                LOW -> highRiskScore.background =
                    context?.getDrawable(R.drawable.bg_exposure_dashboard_low_risk)
                NONE -> highRiskScore.background =
                    context?.getDrawable(R.drawable.bg_exposure_dashboard_number)
            }
            daysSinceLastExposure.text = days ?: "-"
            totalExposures.text = total ?: "-"
            highRiskScore.text = risk ?: "-"
        }
    }
}
