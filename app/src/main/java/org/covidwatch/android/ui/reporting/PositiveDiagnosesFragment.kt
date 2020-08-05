package org.covidwatch.android.ui.reporting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import org.covidwatch.android.R
import org.covidwatch.android.databinding.FragmentPositiveDiagnosesBinding
import org.covidwatch.android.extension.observe
import org.koin.androidx.viewmodel.ext.android.viewModel

class PositiveDiagnosesFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentPositiveDiagnosesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PositiveDiagnosesViewModel by viewModel()
    private val adapter = GroupAdapter<GroupieViewHolder>()

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
            val isModal = arguments?.getBoolean(MODAL) == true

            closeButton.isVisible = !isModal
            handler.isVisible = isModal

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
            observe(positiveDiagnoses) {
                if (it.isNotEmpty()) adapter.clear()

                it.forEach { diagnosis ->
                    adapter.add(
                        ExpandableGroup(DiagnosisItem(requireContext(), diagnosis)).apply {
                            add(DiagnosisDetailsItem(viewModel, diagnosis))
                        }
                    )
                }
                binding.noPastPositiveDiagnoses.isVisible = it.isEmpty()
                binding.pastPositiveDiagnosesList.isVisible = it.isNotEmpty()
            }
        }
    }

    companion object {
        private val MODAL = "modal"

        fun instance() = PositiveDiagnosesFragment().apply {
            arguments = Bundle().apply {
                putBoolean(MODAL, true)
            }
        }
    }
}