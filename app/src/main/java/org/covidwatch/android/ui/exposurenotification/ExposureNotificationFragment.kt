package org.covidwatch.android.ui.exposurenotification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_exposure_notification.*
import org.covidwatch.android.R
import org.covidwatch.android.databinding.DialogPhaPermissionNumberBinding
import org.covidwatch.android.databinding.FragmentExposureNotificationBinding
import org.covidwatch.android.exposurenotification.Failure
import org.covidwatch.android.extension.observe
import org.covidwatch.android.extension.observeEvent
import org.koin.androidx.viewmodel.ext.android.viewModel

class ExposureNotificationFragment : Fragment() {

    private var _binding: FragmentExposureNotificationBinding? = null
    private val binding get() = _binding!!
    private lateinit var startServiceMenuItem: MenuItem

    private val exposureNotificationViewModel: ExposureNotificationViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExposureNotificationBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()

        with(binding) {
            viewModel = exposureNotificationViewModel
            pullToRefresh.setOnRefreshListener {
                exposureNotificationViewModel.downloadDiagnosisKeys()
            }
        }

        with(exposureNotificationViewModel) {
            observe(isRefreshing) {
                binding.pullToRefresh.isRefreshing = it
            }
            observeEvent(status) { handleError(it) }
            observe(exposureServiceRunning) { running ->
                val title = if (running) R.string.stop else R.string.start
                startServiceMenuItem.title = getString(title)
            }
        }
    }

    private fun initToolbar() {
        startServiceMenuItem = toolbar.menu.findItem(R.id.action_start_stop)

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_upload_diagnosis -> {
                    context?.let { context ->
                        val dialogView =
                            DialogPhaPermissionNumberBinding.inflate(
                                LayoutInflater.from(context)
                            )

                        AlertDialog
                            .Builder(context)
                            .setView(dialogView.root)
                            .setPositiveButton(R.string.upload) { _, _ ->
                                exposureNotificationViewModel.uploadDiagnosis()
                            }
                            .setNegativeButton(R.string.cancel, null)
                            .create()
                            .show()
                    }
                }
                R.id.action_start_stop -> exposureNotificationViewModel.startStopService()
            }

            true
        }
    }

    private fun handleError(status: Failure?) {
        when (status) {
            Failure.FailedRejectedOptIn -> TODO()
            Failure.FailedServiceDisabled -> TODO()
            Failure.FailedBluetoothScanningDisabled -> TODO()
            Failure.FailedTemporarilyDisabled -> TODO()
            Failure.FailedInsufficientStorage -> TODO()
            Failure.Failed -> TODO()
        }
    }

    companion object {
        fun newInstance() = ExposureNotificationFragment()
    }
}
