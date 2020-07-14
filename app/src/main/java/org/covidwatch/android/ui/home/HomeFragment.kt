package org.covidwatch.android.ui.home

import android.animation.LayoutTransition
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.covidwatch.android.R
import org.covidwatch.android.data.NextStep
import org.covidwatch.android.databinding.FragmentHomeBinding
import org.covidwatch.android.databinding.ItemNextStepBinding
import org.covidwatch.android.extension.observe
import org.covidwatch.android.extension.observeEvent
import org.covidwatch.android.extension.shareApp
import org.covidwatch.android.ui.BaseFragment
import org.covidwatch.android.ui.name
import org.covidwatch.android.ui.setBackgroundFromRiskLevel
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val viewModel: HomeViewModel by viewModel()

    override fun bind(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding =
        FragmentHomeBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewModel) {
            onStart()
            observeEvent(navigateToOnboarding) {
                findNavController().navigate(R.id.splashFragment)
            }

            observe(region) {
                binding.tvRegion.text = HtmlCompat.fromHtml(
                    getString(R.string.current_region, it.name),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                )
            }

            observe(riskLevel) {
                binding.myRiskLevel.setBackgroundFromRiskLevel(it)
                binding.myRiskLevel.text = HtmlCompat.fromHtml(
                    getString(R.string.my_risk_level, it.name(requireContext())),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                )
            }
            observe(nextSteps, ::bindNextSteps)
            observeEvent(showOnboardingAnimation) {
                lifecycleScope.launch {
                    binding.homeScreenArt.isVisible = false
                    binding.homeScreenContent.layoutTransition = LayoutTransition()
                    delay(1000)
                    binding.homeScreenArt.isVisible = true
                }
            }

            observe(infoBannerState) { banner ->
                when (banner) {
                    is InfoBannerState.Visible -> {
                        binding.infoBanner.isVisible = true
                        binding.infoBanner.setText(banner.text)
                    }
                    InfoBannerState.Hidden -> {
                        binding.infoBanner.isVisible = false
                    }
                }
            }
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
        }
    }

    private fun bindNextSteps(nextSteps: List<NextStep>) {
        val layoutInflater = LayoutInflater.from(context)
        nextSteps.forEach {
            val nextStep = ItemNextStepBinding.inflate(layoutInflater, binding.nextSteps, true)
            with(nextStep) {
                nextSteText.text = it.description
            }
        }
    }
}
