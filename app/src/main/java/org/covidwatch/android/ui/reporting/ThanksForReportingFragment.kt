package org.covidwatch.android.ui.reporting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.navigation.fragment.findNavController
import org.covidwatch.android.R
import org.covidwatch.android.databinding.FragmentThanksForReportingBinding
import org.covidwatch.android.extension.shareApp
import org.covidwatch.android.ui.BaseFragment

class ThanksForReportingFragment : BaseFragment<FragmentThanksForReportingBinding>() {

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentThanksForReportingBinding =
        FragmentThanksForReportingBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeButton.setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }
        binding.shareAppButton.setOnClickListener {
            context?.shareApp()
        }

        val text = "My text <ul><li>bullet one</li><li>bullet two</li></ul>"
        binding.pastPositiveDiagnoses.text = HtmlCompat.fromHtml(
            text, HtmlCompat.FROM_HTML_MODE_COMPACT
        )
    }
}
