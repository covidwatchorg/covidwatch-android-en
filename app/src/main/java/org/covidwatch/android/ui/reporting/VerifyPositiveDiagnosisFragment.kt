package org.covidwatch.android.ui.reporting

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import org.covidwatch.android.R
import org.covidwatch.android.databinding.FragmentVerifyPositiveDiagnosisBinding
import org.covidwatch.android.extension.observe
import org.covidwatch.android.extension.observeEvent
import org.covidwatch.android.ui.BaseFragment
import org.covidwatch.android.ui.util.DateFormatter
import org.koin.android.ext.android.inject
import java.util.*
import java.util.Calendar.DAY_OF_MONTH

class VerifyPositiveDiagnosisFragment : BaseFragment<FragmentVerifyPositiveDiagnosisBinding>() {

    private val viewModel: VerifyPositiveDiagnosisViewModel by inject()

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentVerifyPositiveDiagnosisBinding =
        FragmentVerifyPositiveDiagnosisBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            closeButton.setOnClickListener { findNavController().popBackStack() }

            cbNoSymptoms.setOnCheckedChangeListener { _, noSymptoms ->
                viewModel.noSymptoms(noSymptoms)
            }

            etVerificationCode.addTextChangedListener(afterTextChanged = {
                viewModel.verificationCode(it.toString())
            })

            etSymptomsDate.setOnClickListener { showSymptomsDatePicker() }
            etTestedDate.setOnClickListener { showTestedDatePicker() }

            btnFinishVerification.setOnClickListener {
                viewModel.sharePositiveDiagnosis()
            }
        }

        with(viewModel) {
            observe(readyToSubmit) {
                binding.btnFinishVerification.isEnabled = it
            }

            observeEvent(showThankYou) {
                findNavController().navigate(R.id.thanksForReportingFragment)
            }
        }
    }

    private fun showSymptomsDatePicker() {
        val builder = MaterialDatePicker.Builder.datePicker()
        val constraints = CalendarConstraints.Builder()
        val now = Date().time

        constraints.setValidator(
            BaseDateValidator { it < now }
        )

        val datePicker = builder
            .setCalendarConstraints(constraints.build())
            .build()

        datePicker.addOnPositiveButtonClickListener {
            binding.etSymptomsDate.setText(DateFormatter.format(it))
            viewModel.symptomsStartDate(it)
        }
        datePicker.show(parentFragmentManager, null)
    }

    private fun showTestedDatePicker() {
        val builder = MaterialDatePicker.Builder.datePicker()
        val constraints = CalendarConstraints.Builder()

        val twoWeeksAgo = Calendar.getInstance()
        twoWeeksAgo.add(DAY_OF_MONTH, -14)

        constraints.setValidator(
            BaseDateValidator { it > twoWeeksAgo.timeInMillis && it < Date().time }
        )

        val datePicker = builder
            .setCalendarConstraints(constraints.build())
            .build()

        datePicker.addOnPositiveButtonClickListener {
            binding.etTestedDate.setText(DateFormatter.format(it))
            viewModel.testedDate(it)
        }
        datePicker.show(parentFragmentManager, null)
    }

    @SuppressLint("ParcelCreator")
    internal class BaseDateValidator(private val _isValid: (Long) -> Boolean) :
        CalendarConstraints.DateValidator {

        override fun writeToParcel(dest: Parcel?, flags: Int) = Unit

        override fun isValid(date: Long) = _isValid(date)

        override fun describeContents() = 0
    }
}