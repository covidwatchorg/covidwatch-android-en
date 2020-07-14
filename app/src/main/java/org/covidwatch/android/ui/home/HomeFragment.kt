package org.covidwatch.android.ui.home

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
import org.covidwatch.android.extension.shareApp
import org.covidwatch.android.ui.BaseFragment
import org.covidwatch.android.ui.event.EventObserver
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val viewModel: HomeViewModel by viewModel()

    override fun bind(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding =
        FragmentHomeBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewModel) {
            onStart()
            navigateToOnboardingEvent.observe(viewLifecycleOwner, EventObserver {
                findNavController().navigate(R.id.splashFragment)
            })

            observe(nextSteps, ::bindNextSteps)

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
