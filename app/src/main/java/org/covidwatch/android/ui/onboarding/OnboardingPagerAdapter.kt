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
                R.string.how_it_works_title_1,
                R.drawable.how_it_works_1,
                R.string.how_it_works_text_1
            )
            1 -> OnboardingPageParameters(
                R.string.how_it_works_title_2,
                R.drawable.how_it_works_2,
                R.string.how_it_works_text_2
            )
            2 -> OnboardingPageParameters(
                R.string.how_it_works_title_3,
                R.drawable.how_it_works_3,
                R.string.how_it_works_text_3
            )
            3 -> OnboardingPageParameters(
                R.string.how_it_works_title_4,
                R.drawable.how_it_works_4,
                R.string.how_it_works_text_4
            )
            else -> OnboardingPageParameters(
                R.string.how_it_works_title_1,
                R.drawable.how_it_works_1,
                R.string.how_it_works_text_1
            )
        }
    }
}