package org.covidwatch.android.ui.reporting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import org.covidwatch.android.databinding.FragmentNotifyOthersBinding
import org.covidwatch.android.extension.observe
import org.covidwatch.android.ui.BaseFragment
import org.covidwatch.android.ui.Notifications
import org.koin.android.ext.android.inject

class NotifyOthersFragment : BaseFragment<FragmentNotifyOthersBinding>() {

    private val viewModel: NotifyOthersViewModel by inject()
    private val notifications: Notifications by inject()

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
            observe(uploadingReport) { uploading ->
                if (uploading) {
                    notifications.postUploadingReportNotification()
                } else {
                    notifications.hideUploadingReportNotification()
                }
            }
            observe(positiveDiagnosis) { adapter.setItems(it) }
        }
    }


    private fun dividerItemDecoration(): RecyclerView.ItemDecoration {
        return DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
    }
}