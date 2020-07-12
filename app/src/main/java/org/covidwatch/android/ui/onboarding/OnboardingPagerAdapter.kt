package org.covidwatch.android.ui.onboarding

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import org.covidwatch.android.R

class OnboardingPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return OnboardingPageFragment().apply {
            arguments = bundleOf(ONBOARDING_PAGE_PARAMETERS_KEY to howItWorksParameters(position))
        }
    }

    private fun howItWorksParameters(position: Int): OnboardingPageParameters {
        return when (position) {
            0 -> OnboardingPageParameters(
                R.string.how_it_works_title1,
                R.drawable.how_it_works_1,
                R.string.stay_anonymous_subtitle
            )
            1 -> OnboardingPageParameters(
                R.string.diagnosis_reports_title,
                R.drawable.how_it_works_2,
                R.string.diagnosis_reports_subtitle
            )
            2 -> OnboardingPageParameters(
                R.string.exposure_alerts_title,
                R.drawable.how_it_works_3,
                R.string.exposure_alerts_subtitle
            )
            3 -> OnboardingPageParameters(
                R.string.safe_communities_title,
                R.drawable.how_it_works_4,
                R.string.safe_communities_subtitle
            )
            else -> OnboardingPageParameters(
                R.string.how_it_works_title1,
                R.drawable.how_it_works_1,
                R.string.stay_anonymous_subtitle
            )
        }
    }
}