package org.covidwatch.android.ui.reporting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import org.covidwatch.android.databinding.FragmentNotifyOthersBinding
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.extension.observe
import org.covidwatch.android.extension.observeEvent
import org.covidwatch.android.ui.BaseFragment
import org.koin.android.ext.android.inject

class NotifyOthersFragment : BaseFragment<FragmentNotifyOthersBinding>() {

    private val viewModel: NotifyOthersViewModel by inject()

    private val adapter = PositiveDiagnosisAdapter()

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNotifyOthersBinding =
        FragmentNotifyOthersBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            closeButton.setOnClickListener {
                findNavController().popBackStack()
            }
            sharePositiveDiagnosisButton.setOnClickListener {
                viewModel.sharePositiveDiagnosis()
            }

            pastPositiveDiagnosesList.addItemDecoration(dividerItemDecoration())
            pastPositiveDiagnosesList.adapter = adapter
        }

        with(viewModel) {
            observeEvent(status) { handleError(it) }
            observeEvent(resolvable) { resolvable ->
                resolvable.apiException.status.startResolutionForResult(
                    activity, resolvable.requestCode
                )
            }
            observe(positiveDiagnosis) { adapter.setItems(it) }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        lifecycleScope.launch {
            viewModel.handleResolution(requestCode, resultCode)
        }
    }

    private fun handleError(status: ENStatus?) {
        when (status) {
            ENStatus.FailedRejectedOptIn -> TODO()
            ENStatus.FailedServiceDisabled -> TODO()
            ENStatus.FailedBluetoothScanningDisabled -> TODO()
            ENStatus.FailedTemporarilyDisabled -> TODO()
            ENStatus.FailedInsufficientStorage -> TODO()
            ENStatus.Failed -> TODO()
        }
    }

    private fun dividerItemDecoration(): RecyclerView.ItemDecoration {
        return DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
    }
}