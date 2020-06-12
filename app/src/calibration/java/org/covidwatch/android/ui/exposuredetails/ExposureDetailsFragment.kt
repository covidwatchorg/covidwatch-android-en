package org.covidwatch.android.ui.exposuredetails

import android.os.Bundle
import android.view.View
import org.covidwatch.android.databinding.ExposureInformationDetailsBinding

class ExposureDetailsFragment : BaseExposureDetailsFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            args.exposureInformation?.let {
                val demoExposureDetails = ExposureInformationDetailsBinding.inflate(
                    layoutInflater,
                    exposureDetailsList,
                    false
                )
                demoExposureDetails.exposureInformation = it
                exposureDetailsList.addView(demoExposureDetails.root, 1)
            }
        }
    }
}
