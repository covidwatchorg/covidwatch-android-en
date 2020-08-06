package org.covidwatch.android.ui.exposures

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import org.covidwatch.android.databinding.FragmentExposuresBinding
import org.covidwatch.android.extension.observe
import org.covidwatch.android.extension.observeEvent
import org.covidwatch.android.ui.BaseViewModelFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ExposuresFragment : BaseViewModelFragment<FragmentExposuresBinding, ExposuresViewModel>() {

    override val viewModel: ExposuresViewModel by viewModel()
    private val adapter = GroupAdapter<GroupieViewHolder>()

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
            observe(exposureInfo) { exposures ->
                if (exposures.isNotEmpty()) adapter.clear()

                exposures.forEach { exposure ->
                    adapter.add(
                        ExpandableGroup(ExposureItem(exposure)).apply {
                            add(ExposureDetailsItem(exposure))
                        }
                    )
                }

                if (exposures.isNotEmpty()) adapter.add(FooterItem())
            }
            observeEvent(showExposureDetails) {
                val action =
                    ExposuresFragmentDirections.exposureDetails(it)
                findNavController().navigate(action)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.start()
    }
}
