package org.covidwatch.android.ui.onboarding

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import org.covidwatch.android.R

class OnboardingPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Fragment(R.layout.fragment_onboarding_start)
            else -> HowItWorksFragment().apply {
                arguments = bundleOf(HOW_IT_WORKS_PARAMETER_KEY to howItWorksParameters(position))
            }
        }
    }

    private fun howItWorksParameters(position: Int): HowItWorksParameters {
        return when (position) {
            1 -> HowItWorksParameters(
                R.string.always_anonymous_title,
                R.drawable.ic_how_it_works_01,
                R.string.always_anonymous_subtitle
            )
            2 -> HowItWorksParameters(
                R.string.diagnosis_reports_title,
                R.drawable.ic_how_it_works_02,
                R.string.diagnosis_reports_subtitle
            )
            3 -> HowItWorksParameters(
                R.string.exposure_alerts_title,
                R.drawable.ic_how_it_works_03,
                R.string.exposure_alerts_subtitle
            )
            4 -> HowItWorksParameters(
                R.string.safe_communities_title,
                R.drawable.ic_how_it_works_04,
                R.string.safe_communities_subtitle
            )
            else -> HowItWorksParameters(
                R.string.always_anonymous_title,
                R.drawable.ic_how_it_works_01,
                R.string.always_anonymous_subtitle
            )
        }
    }
}