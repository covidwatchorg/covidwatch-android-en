package org.covidwatch.android.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import org.covidwatch.android.R
import org.covidwatch.android.databinding.FragmentEnableExposureNotificationsBinding
import org.covidwatch.android.extension.observeEvent
import org.covidwatch.android.ui.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class EnableExposureNotificationsFragment :
    BaseFragment<FragmentEnableExposureNotificationsBinding>() {

    private val viewModel: EnableExposureNotificationsViewModel by viewModel()

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
            findNavController().popBackStack(R.id.homeFragment, false)
        }

        with(viewModel) {
            observeEvent(exposureNotificationResult) {
                findNavController().popBackStack(R.id.homeFragment, false)
            }
            observeEvent(resolvable) { resolvable ->
                resolvable.apiException.status.startResolutionForResult(
                    activity, resolvable.requestCode
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        lifecycleScope.launch {
            viewModel.handleResolution(requestCode, resultCode)
        }
    }
}