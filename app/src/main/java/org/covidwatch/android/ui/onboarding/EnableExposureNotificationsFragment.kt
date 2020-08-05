package org.covidwatch.android.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import org.covidwatch.android.R
import org.covidwatch.android.databinding.FragmentEnableExposureNotificationsBinding
import org.covidwatch.android.extension.observeEvent
import org.covidwatch.android.ui.BaseViewModelFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class EnableExposureNotificationsFragment :
    BaseViewModelFragment<FragmentEnableExposureNotificationsBinding, EnableExposureNotificationsViewModel>() {

    override val viewModel: EnableExposureNotificationsViewModel by viewModel()

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEnableExposureNotificationsBinding =
        FragmentEnableExposureNotificationsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.enableButton.setOnClickListener {
            viewModel.onEnableClicked()
        }

        binding.notNowButton.setOnClickListener {
            viewModel.notNowClicked()
        }

        with(viewModel) {
            observeEvent(showHome) {
                findNavController().popBackStack()
            }
            observeEvent(continueOnboarding) {
                val args = Bundle()
                args.putBoolean("onboarding", true)
                findNavController().popBackStack(R.id.howItWorksFragment, true)
                findNavController().navigate(R.id.homeFragment)
                findNavController().navigate(R.id.selectRegionFragment, args)
            }
        }
    }
}