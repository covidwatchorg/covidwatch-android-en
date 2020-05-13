package org.covidwatch.android.ui.reporting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.covidwatch.android.data.PositiveDiagnosis
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository

class ReportingViewModel(
    private val positiveDiagnosisRepository: PositiveDiagnosisRepository
) : ViewModel() {

    private val _positiveDiagnosis = MutableLiveData<List<PositiveDiagnosis>>()
    private val positiveDiagnosis: LiveData<List<PositiveDiagnosis>> get() = _positiveDiagnosis

    init {
    }
}