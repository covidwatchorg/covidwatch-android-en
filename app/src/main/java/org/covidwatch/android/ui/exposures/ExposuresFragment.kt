package org.covidwatch.android.ui.exposures

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import org.covidwatch.android.databinding.FragmentExposuresBinding
import org.covidwatch.android.extension.observeEvent
import org.covidwatch.android.ui.BaseViewModelFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ExposuresFragment : BaseViewModelFragment<FragmentExposuresBinding, ExposuresViewModel>() {

    override val viewModel: ExposuresViewModel by viewModel()

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
            viewModel = this@ExposuresFragment.viewModel
            executePendingBindings()

            btnClose.setOnClickListener {
                findNavController().popBackStack()
            }
        }
        with(viewModel) {
            observeEvent(showExposureDetails) {
                val action =
                    ExposuresFragmentDirections.actionExposuresFragmentToExposureDetailsFragment(it)
                findNavController().navigate(action)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.start()
    }
}
