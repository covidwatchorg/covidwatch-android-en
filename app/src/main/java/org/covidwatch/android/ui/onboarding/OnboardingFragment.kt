package org.covidwatch.android.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import org.covidwatch.android.R
import org.covidwatch.android.databinding.FragmentOnboardingBinding
import org.covidwatch.android.ui.BaseFragment

class OnboardingFragment : BaseFragment<FragmentOnboardingBinding>() {

    private lateinit var pagerAdapter: OnboardingPagerAdapter
    private var isFirstPage = true
    private var isLastPage = false
    private val args: OnboardingFragmentArgs by navArgs()

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            isLastPage = position == pagerAdapter.itemCount - 1
            isFirstPage = position == 0

            updateActionLayout()
        }
    }

    private fun updateActionLayout() {
        binding.btnPreviousOnboardingScreen.visibility =
            if (isFirstPage) View.INVISIBLE else View.VISIBLE

        binding.onboardingPageIndicator.isVisible = !isLastPage
        binding.continueSetupButton.isVisible = isLastPage
    }

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOnboardingBinding = FragmentOnboardingBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pagerAdapter = OnboardingPagerAdapter(this)
        updateActionLayout()

        with(binding) {

            viewPager.adapter = pagerAdapter
            viewPager.registerOnPageChangeCallback(onPageChangeCallback)
            TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()

            btnPreviousOnboardingScreen.setOnClickListener {
                viewPager.currentItem = tabLayout.selectedTabPosition - 1
            }

            btnNextOnboardingScreen.setOnClickListener {
                viewPager.currentItem = tabLayout.selectedTabPosition + 1
            }

            if (args.onboarding) {
                continueSetupButton.setText(R.string.continue_setup_button)
                continueSetupButton.setOnClickListener {
                    // Remove previous onboarding fragments from the stack so we can't go back.
                    findNavController().popBackStack(R.id.homeFragment, false)
                    findNavController().navigate(R.id.enableExposureNotificationsFragment)
                }
            } else {
                continueSetupButton.setText(R.string.button_finish)
                continueSetupButton.setOnClickListener {
                    findNavController().popBackStack(R.id.homeFragment, false)
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isFirstPage) {
                        findNavController().popBackStack()
                    } else {
                        binding.viewPager.currentItem = binding.tabLayout.selectedTabPosition - 1
                    }
                }
            })
    }

    override fun onDestroyView() {
        binding.viewPager.unregisterOnPageChangeCallback(onPageChangeCallback)
        super.onDestroyView()
    }
}