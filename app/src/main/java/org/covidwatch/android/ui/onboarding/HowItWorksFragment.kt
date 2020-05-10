package org.covidwatch.android.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.covidwatch.android.databinding.FragmentHowItWorksBinding
import org.covidwatch.android.ui.BaseFragment

internal const val HOW_IT_WORKS_PARAMETER_KEY = "HOW_IT_WORKS_PARAMETER_KEY"

class HowItWorksFragment : BaseFragment<FragmentHowItWorksBinding>() {

    override fun binding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHowItWorksBinding = FragmentHowItWorksBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parameters: HowItWorksParameters =
            requireNotNull(arguments?.getParcelable(HOW_IT_WORKS_PARAMETER_KEY))

        binding.title.setText(parameters.title)
        binding.image.setImageResource(parameters.image)
        binding.subtitle.setText(parameters.subtitle)
    }
}