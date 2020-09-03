package org.covidwatch.android.ui.exposures

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import org.covidwatch.android.R
import org.covidwatch.android.data.CovidExposureInformation
import org.covidwatch.android.databinding.FragmentExposuresBinding
import org.covidwatch.android.extension.observe
import org.covidwatch.android.extension.observeEvent
import org.covidwatch.android.ui.BaseViewModelFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseExposuresFragment :
    BaseViewModelFragment<FragmentExposuresBinding, ExposuresViewModel>() {

    override val viewModel: ExposuresViewModel by viewModel()
    protected val adapter = GroupAdapter<GroupieViewHolder>()

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentExposuresBinding {
        return FragmentExposuresBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            lifecycleOwner = this@BaseExposuresFragment
            viewModel = this@BaseExposuresFragment.viewModel
            executePendingBindings()

            exposureInfoList.adapter = adapter
            exposureInfoList.addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )
            btnClose.setOnClickListener {
                findNavController().popBackStack()
            }
        }
        with(viewModel) {
            observe(exposureInfo, this@BaseExposuresFragment::exposuresLoaded)

            observe(enEnabled) { enabled ->
                if (enabled) {
                    binding.exposureNotificationsSubtitle.setText(R.string.exposures_notifications_on_subtitle)
                } else {
                    binding.exposureNotificationsSubtitle.setText(R.string.exposures_notifications_off_subtitle)
                }
            }
            observeEvent(showExposureDetails) {
                val action =
                    ExposuresFragmentDirections.exposureDetails(it)
                findNavController().navigate(action)
            }
        }
    }

    abstract fun exposuresLoaded(exposures: List<CovidExposureInformation>)

    override fun onResume() {
        super.onResume()
        viewModel.start()
    }
}
