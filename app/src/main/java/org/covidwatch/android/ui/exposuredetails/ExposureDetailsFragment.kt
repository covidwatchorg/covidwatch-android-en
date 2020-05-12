package org.covidwatch.android.ui.exposuredetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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
            binding.exposureInformation = it
        }

        with(binding) {
            btnClose.setOnClickListener { findNavController().popBackStack() }

            //TODO:
            btnSharePositiveDiagnosis.setOnClickListener {
                Toast.makeText(context, "Not implemented", Toast.LENGTH_SHORT).show()
            }
            btnFindTestSite.setOnClickListener {
                Toast.makeText(context, "Not implemented", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
