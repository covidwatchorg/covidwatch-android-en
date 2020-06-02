package org.covidwatch.android.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.covidwatch.android.R
import org.covidwatch.android.databinding.FragmentEnableExposureNotificationsBinding
import org.covidwatch.android.exposurenotification.ENStatus
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
            observeEvent(status) {
                when (it) {
                    ENStatus.FailedInsufficientStorage -> {
                        val snackbar = Snackbar.make(
                            binding.root,
                            R.string.insufficient_storage,
                            LENGTH_INDEFINITE
                        )
                        snackbar.setAction(R.string.ok) { snackbar.dismiss() }
                        snackbar.show()
                    }
                    ENStatus.Failed -> {
                        Toast.makeText(
                            context,
                            R.string.unknown_error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
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