package org.covidwatch.android.ui.exposuredetails

import android.os.Bundle
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.util.LinkifyCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.covidwatch.android.R
import org.covidwatch.android.databinding.FragmentExposureDetailsBinding
import org.covidwatch.android.ui.BaseFragment

class ExposureDetailsFragment : BaseFragment<FragmentExposureDetailsBinding>() {

    private val args: ExposureDetailsFragmentArgs by navArgs()

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentExposureDetailsBinding =
        FragmentExposureDetailsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.exposureInformation?.let {
            binding.exposure = it
        }

        with(binding) {
            LinkifyCompat.addLinks(followGuidance, Linkify.WEB_URLS)
            LinkifyCompat.addLinks(callPha, Linkify.PHONE_NUMBERS)

            btnClose.setOnClickListener { findNavController().popBackStack() }

            btnSharePositiveDiagnosis.setOnClickListener {
                findNavController().navigate(R.id.notifyOthersFragment)
            }

            exposureDetails.exposureRiskInfo.setOnClickListener {
                Toast.makeText(context, "Not implemented", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
