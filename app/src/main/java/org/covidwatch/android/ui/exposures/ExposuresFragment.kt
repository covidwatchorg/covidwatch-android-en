package org.covidwatch.android.ui.exposures

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import org.covidwatch.android.databinding.FragmentExposuresBinding
import org.covidwatch.android.extension.observeEvent
import org.covidwatch.android.ui.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ExposuresFragment : BaseFragment<FragmentExposuresBinding>() {

    private val exposuresViewModel: ExposuresViewModel by viewModel()

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentExposuresBinding {
        return FragmentExposuresBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            lifecycleOwner = this@ExposuresFragment
            viewModel = exposuresViewModel
            executePendingBindings()

            btnClose.setOnClickListener {
                findNavController().popBackStack()
            }
        }
        with(exposuresViewModel) {
            observeEvent(showExposureDetails) {
                val action =
                    ExposuresFragmentDirections.actionExposuresFragmentToExposureDetailsFragment(it)
                findNavController().navigate(action)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        exposuresViewModel.start()
    }
}
