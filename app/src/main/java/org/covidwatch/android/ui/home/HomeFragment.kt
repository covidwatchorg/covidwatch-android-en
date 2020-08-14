package org.covidwatch.android.ui.home

import android.animation.LayoutTransition
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.delay
import org.covidwatch.android.R
import org.covidwatch.android.data.NextStep
import org.covidwatch.android.data.NextStepType
import org.covidwatch.android.data.RiskLevel
import org.covidwatch.android.databinding.FragmentHomeBinding
import org.covidwatch.android.databinding.ItemNextStepBinding
import org.covidwatch.android.extension.observe
import org.covidwatch.android.extension.observeEvent
import org.covidwatch.android.extension.shareApp
import org.covidwatch.android.ui.*
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
                findNavController().popBackStack()
                findNavController().navigate(R.id.splashFragment)
            }

            observe(region) {
                binding.tvRegion.setRegion(it)
                binding.toolbar.logo = ContextCompat.getDrawable(view.context, it.logo)

                if (it.isDisabled) {
                    binding.actionLayoutTitle.isVisible = false

                    binding.actionLayoutInfo.setText(R.string.choose_another_region_info)
                    binding.actionLayoutBtn.setText(R.string.btn_choose_different_region)
                    binding.actionLayoutBtn.setOnClickListener {
                        findNavController().navigate(R.id.selectRegionFragment)
                    }
                } else {
                    binding.actionLayoutTitle.isVisible = true

                    binding.actionLayoutInfo.setText(R.string.positive_diagnosis_info)
                    binding.actionLayoutBtn.setText(R.string.btn_how_to_share_diagnosis)
                    binding.actionLayoutBtn.setOnClickListener {
                        findNavController().navigate(R.id.notifyOthersFragment)
                    }
                }
            }

            observe(riskLevel) {
                when (it) {
                    RiskLevel.VERIFIED_POSITIVE -> {
                        binding.nextStepsMetaTitle.isVisible = false
                        binding.nextStepsTitle.setText(R.string.next_steps_verified_positive)

                        // TODO: 14.08.2020 setting visibility fo actionLayoutTitle in different places is
                        // a flawed solution as it depends on the order of the code execution of those places:
                        // region first and riskLevel second
                        binding.actionLayoutTitle.isVisible = false

                        binding.actionLayoutInfo.isVisible = false
                        binding.actionLayoutBtn.isVisible = false
                    }
                    RiskLevel.HIGH -> {
                        binding.nextStepsMetaTitle.isVisible = false
                        binding.nextStepsTitle.setText(R.string.next_steps_exposures)

                        binding.actionLayoutInfo.isVisible = true
                        binding.actionLayoutBtn.isVisible = true
                    }
                    RiskLevel.LOW -> {
                        binding.nextStepsMetaTitle.isVisible = true
                        binding.nextStepsTitle.setText(R.string.next_steps_no_exposures)

                        binding.actionLayoutInfo.isVisible = true
                        binding.actionLayoutBtn.isVisible = true
                    }
                }

                binding.myRiskLevel.setBackgroundFromRiskLevel(it)
                binding.myRiskLevel.text =
                    getString(R.string.my_risk_level, it.name(requireContext()))
            }
            observe(nextSteps, ::bindNextSteps)
            observeEvent(showOnboardingAnimation) {
                lifecycleScope.launchWhenResumed {
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
            menu.setOnClickListener {
                findNavController().navigate(R.id.menuFragment)
            }
            tvRegion.setOnClickListener { findNavController().navigate(R.id.selectRegionFragment) }
            infoBanner.setOnClickListener {
                findNavController().navigate(R.id.enableExposureNotificationsFragment)
            }
        }
    }

    private fun bindNextSteps(nextSteps: List<NextStep>) {
        val layoutInflater = LayoutInflater.from(context)
        binding.nextSteps.removeAllViews()
        nextSteps.forEach { nextStep ->
            val view = ItemNextStepBinding.inflate(layoutInflater, binding.nextSteps, true)
            with(view) {
                nextStepText.text = nextStep.description
                when (nextStep.type) {
                    NextStepType.INFO -> {
                        nextStepIcon.setImageResource(R.drawable.ic_info_filled)
                    }
                    NextStepType.PHONE -> {
                        nextStepIcon.setImageResource(R.drawable.ic_next_step_phone)
                        root.addCircleRipple()
                        root.setOnClickListener {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse(nextStep.url)
                            }
                            if (intent.resolveActivity(view.root.context.packageManager) != null) {
                                startActivity(intent)
                            }
                        }
                    }
                    NextStepType.WEBSITE -> {
                        root.addCircleRipple()
                        root.setOnClickListener {
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(nextStep.url))
                            startActivity(browserIntent)
                        }
                        nextStepIcon.setImageResource(R.drawable.ic_next_step_web)
                    }
                    NextStepType.SHARE -> {
                        root.addCircleRipple()
                        root.setOnClickListener {
                            context?.shareApp()
                        }
                        nextStepIcon.setImageResource(R.drawable.ic_next_step_share)
                    }
                    NextStepType.SELECT_REGION -> {
                        root.addCircleRipple()
                        root.setOnClickListener {
                            findNavController().navigate(R.id.selectRegionFragment)
                        }
                        nextStepIcon.setImageResource(R.drawable.ic_region)
                    }
                }
            }
        }
    }

    private fun View.addCircleRipple() = with(TypedValue()) {
        context.theme.resolveAttribute(
            android.R.attr.selectableItemBackgroundBorderless,
            this,
            true
        )
        foreground = ContextCompat.getDrawable(requireContext(), resourceId)
    }
}
