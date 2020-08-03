package org.covidwatch.android.ui.reporting

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import org.covidwatch.android.R
import org.covidwatch.android.databinding.FragmentVerifyPositiveDiagnosisBinding
import org.covidwatch.android.extension.observe
import org.covidwatch.android.extension.observeEvent
import org.covidwatch.android.ui.BaseViewModelFragment
import org.covidwatch.android.ui.util.DateFormatter
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import java.util.*
import java.util.Calendar.DAY_OF_MONTH

class VerifyPositiveDiagnosisFragment :
    BaseViewModelFragment<FragmentVerifyPositiveDiagnosisBinding, VerifyPositiveDiagnosisViewModel>() {

    override val viewModel: VerifyPositiveDiagnosisViewModel by stateViewModel()

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
                etSymptomsDate.isEnabled = !noSymptoms
                noSymptomsLayout.isVisible = noSymptoms
                viewModel.noSymptoms(noSymptoms)
            }

            cbNoExposedDate.setOnCheckedChangeListener { _, noExposedDate ->
                etExposedDate.isEnabled = !noExposedDate
                viewModel.noInfectionDate(noExposedDate)
            }

            ivTestVerificationCodeInfo.setOnClickListener {
                VerificationCodeHelpDialog().show(childFragmentManager, null)
            }
            etVerificationCode.addTextChangedListener(afterTextChanged = {
                viewModel.verificationCode(it.toString())
            })

            etSymptomsDate.setOnClickListener {
                showDatePicker {
                    binding.etSymptomsDate.setText(DateFormatter.format(it))
                    viewModel.symptomDate(it)
                }
            }
            etTestedDate.setOnClickListener {
                showDatePicker {
                    binding.etTestedDate.setText(DateFormatter.format(it))
                    viewModel.testDate(it)
                }
            }
            etExposedDate.setOnClickListener {
                showDatePicker {
                    binding.etExposedDate.setText(DateFormatter.format(it))
                    viewModel.infectionDate(it)
                }
            }

            btnFinishVerification.setOnClickListener {
                viewModel.sharePositiveDiagnosis()
            }
        }

        with(viewModel) {
            observe(readyToSubmit) {
                binding.btnFinishVerification.isVisible = it
            }
            observe(uploading) {
                binding.uploadProgress.isVisible = it
                // Disable the button while we uploading
                binding.btnFinishVerification.isEnabled = !it
            }
            observeEvent(showThankYou) {
                findNavController().navigate(R.id.thanksForReportingFragment)
            }
        }
    }

    private fun showDatePicker(selectedDate: (Long) -> Unit) {
        val builder = MaterialDatePicker.Builder.datePicker()
        val constraints = CalendarConstraints.Builder()

        val twoWeeksAgo = Calendar.getInstance()
        twoWeeksAgo.add(DAY_OF_MONTH, -14)

        val now = Date().time
        constraints.setValidator(
            BaseDateValidator { it > twoWeeksAgo.timeInMillis && it < now }
        )

        val datePicker = builder
            .setCalendarConstraints(constraints.build())
            .build()

        datePicker.addOnPositiveButtonClickListener { selectedDate(it) }
        datePicker.show(childFragmentManager, null)
    }

    @SuppressLint("ParcelCreator")
    internal class BaseDateValidator(private val _isValid: (Long) -> Boolean) :
        CalendarConstraints.DateValidator {

        override fun writeToParcel(dest: Parcel?, flags: Int) = Unit

        override fun isValid(date: Long) = _isValid(date)

        override fun describeContents() = 0
    }
}