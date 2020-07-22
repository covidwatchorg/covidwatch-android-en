package org.covidwatch.android.ui.reporting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import org.covidwatch.android.R
import org.covidwatch.android.databinding.FragmentNotifyOthersBinding
import org.covidwatch.android.extension.observeEvent
import org.covidwatch.android.ui.BaseViewModelFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


open class BaseNotifyOthersFragment :
    BaseViewModelFragment<FragmentNotifyOthersBinding, NotifyOthersViewModel>() {

    override val viewModel: NotifyOthersViewModel by viewModel()

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

            btnCodeExplanation.setOnClickListener {
                VerificationCodeHelpDialog().show(childFragmentManager, null)
            }

            sharePositiveDiagnosisButton.setOnClickListener {
                viewModel.sharePositiveDiagnosis()
            }
        }

        with(viewModel) {
            observeEvent(openVerificationScreen) { findNavController().navigate(R.id.verifyPositiveDiagnosisFragment) }
        }
    }
}