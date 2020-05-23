package org.covidwatch.android.ui.reporting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import org.covidwatch.android.R
import org.covidwatch.android.databinding.FragmentNotifyOthersBinding
import org.covidwatch.android.domain.UploadDiagnosisKeysUseCase
import org.covidwatch.android.extension.launchUseCase
import org.covidwatch.android.ui.BaseFragment
import org.koin.android.ext.android.inject

class NotifyOthersFragment : BaseFragment<FragmentNotifyOthersBinding>() {

    private val uploadDiagnosisKeysUseCase: UploadDiagnosisKeysUseCase by inject()

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNotifyOthersBinding =
        FragmentNotifyOthersBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.sharePositiveDiagnosisButton.setOnClickListener {
            lifecycleScope.launchUseCase(uploadDiagnosisKeysUseCase)
        }
        setupList()
    }

    private fun setupList() {
        val fakeItems = listOf(
            PositiveDiagnosisItem(
                TestStatus.NeedsVerification,
                getString(R.string.test_date_fmt, "April 20, 2020")
            ),
            PositiveDiagnosisItem(
                TestStatus.Verified,
                getString(R.string.test_date_fmt, "April 20, 2020")
            )
        )
        binding.pastPositiveDiagnosesList.addItemDecoration(dividerItemDecoration())
        binding.pastPositiveDiagnosesList.adapter = PositiveDiagnosisAdapter(fakeItems)
    }

    private fun dividerItemDecoration(): RecyclerView.ItemDecoration {
        return DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
    }
}