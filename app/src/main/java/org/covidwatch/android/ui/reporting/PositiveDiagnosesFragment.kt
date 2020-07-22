package org.covidwatch.android.ui.reporting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.covidwatch.android.R
import org.covidwatch.android.databinding.FragmentPositiveDiagnosesBinding
import org.covidwatch.android.extension.observe
import org.koin.androidx.viewmodel.ext.android.viewModel

class PositiveDiagnosesFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentPositiveDiagnosesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PositiveDiagnosesViewModel by viewModel()
    private val adapter = PositiveDiagnosisAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPositiveDiagnosesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            closeButton.setOnClickListener {
                if (findNavController().currentBackStackEntry?.destination?.id == R.id.positiveDiagnosesFragment)
                    findNavController().popBackStack()
                else dismiss()
            }

            pastPositiveDiagnosesList.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            pastPositiveDiagnosesList.adapter = adapter
        }

        with(viewModel) {
            adapter.setViewModel(this)
            observe(positiveDiagnoses) {
                adapter.setItems(it)
                binding.noPastPositiveDiagnoses.isVisible = it.isEmpty()
                binding.pastPositiveDiagnosesList.isVisible = it.isNotEmpty()
            }
        }
    }
}