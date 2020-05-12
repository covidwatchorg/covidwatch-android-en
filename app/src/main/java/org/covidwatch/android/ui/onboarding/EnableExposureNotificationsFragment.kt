package org.covidwatch.android.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import org.covidwatch.android.R
import org.covidwatch.android.databinding.FragmentEnableExposureNotificationsBinding
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.ui.BaseFragment
import org.covidwatch.android.ui.event.EventObserver
import org.koin.androidx.viewmodel.ext.android.viewModel

class EnableExposureNotificationsFragment :
    BaseFragment<FragmentEnableExposureNotificationsBinding>() {

    private val enableExposureNotificationsViewModel: EnableExposureNotificationsViewModel by viewModel()

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEnableExposureNotificationsBinding =
        FragmentEnableExposureNotificationsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.enableButton.setOnClickListener {
            enableExposureNotificationsViewModel.onEnableClicked()
        }

        binding.notNowButton.setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }

        enableExposureNotificationsViewModel.exposureNotificationResult.observe(
            viewLifecycleOwner,
            EventObserver {
                val enStatus = it.left
                if (enStatus == ENStatus.SUCCESS) {
                    findNavController().popBackStack(R.id.homeFragment, false)
                    return@EventObserver
                }

                showError(enStatus)
            })
    }

    private fun showError(enStatus: ENStatus?) {
        // TODO: handle ENStatus
        Snackbar.make(binding.root, R.string.generic_error_message, Snackbar.LENGTH_LONG).show()
    }
}