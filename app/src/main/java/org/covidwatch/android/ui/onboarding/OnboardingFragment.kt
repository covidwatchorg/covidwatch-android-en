package org.covidwatch.android.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import org.covidwatch.android.R
import org.covidwatch.android.databinding.FragmentOnboardingBinding
import org.covidwatch.android.ui.BaseFragment

class OnboardingFragment : BaseFragment<FragmentOnboardingBinding>() {

    private lateinit var pagerAdapter: OnboardingPagerAdapter

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            val isLastPage = position == pagerAdapter.itemCount - 1
            binding.onboardingPageIndicator.isVisible = !isLastPage
            binding.continueSetupButton.isVisible = isLastPage
        }
    }

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOnboardingBinding = FragmentOnboardingBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pagerAdapter = OnboardingPagerAdapter(this)

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

            continueSetupButton.setOnClickListener {
                // Remove previous onboarding fragments from the stack so we can't go back.
                findNavController().popBackStack(R.id.homeFragment, false)
                findNavController().navigate(R.id.enableExposureNotificationsFragment)
            }
        }
    }

    override fun onDestroyView() {
        binding.viewPager.unregisterOnPageChangeCallback(onPageChangeCallback)
        super.onDestroyView()
    }
}