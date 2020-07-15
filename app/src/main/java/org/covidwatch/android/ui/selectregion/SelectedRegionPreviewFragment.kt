package org.covidwatch.android.ui.selectregion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import org.covidwatch.android.R
import org.covidwatch.android.data.pref.PreferenceStorage
import org.covidwatch.android.databinding.FragmentSelectedRegionPreviewBinding
import org.covidwatch.android.ui.BaseFragment
import org.koin.android.ext.android.inject


class SelectedRegionPreviewFragment : BaseFragment<FragmentSelectedRegionPreviewBinding>() {

    private val prefs: PreferenceStorage by inject()

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSelectedRegionPreviewBinding =
        FragmentSelectedRegionPreviewBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (prefs.region.id) {
            0 -> binding.regionLogo.setImageResource(R.drawable.cw_big_logo)
            1 -> binding.regionLogo.setImageResource(R.drawable.uoa_logo)
        }

        binding.btnContinue.setOnClickListener {
            findNavController().navigate(R.id.howItWorksFragment)
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
