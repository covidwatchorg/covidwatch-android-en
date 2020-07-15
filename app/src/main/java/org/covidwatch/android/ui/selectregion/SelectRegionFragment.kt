package org.covidwatch.android.ui.selectregion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import org.covidwatch.android.R
import org.covidwatch.android.databinding.FragmentSelectRegionBinding
import org.covidwatch.android.extension.observe
import org.covidwatch.android.ui.BaseFragment
import org.koin.android.ext.android.inject


class SelectRegionFragment : BaseFragment<FragmentSelectRegionBinding>() {

    private val viewModel: SelectRegionViewModel by inject()

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSelectRegionBinding =
        FragmentSelectRegionBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe(viewModel.regions) {
            val adapter = ArrayAdapter(requireContext(), R.layout.item_region_name, it)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            binding.spinnerRegions.adapter = adapter
            binding.spinnerRegions.setSelection(viewModel.selectedRegion)
            binding.spinnerRegions.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) = viewModel.selectedRegion(position)
                }
        }

        binding.btnContinue.setOnClickListener {
            findNavController().navigate(R.id.selectedRegionPreviewFragment)
        }
    }
}
