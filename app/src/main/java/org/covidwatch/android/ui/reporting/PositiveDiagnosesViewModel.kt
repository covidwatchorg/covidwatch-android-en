package org.covidwatch.android.ui.reporting

import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.covidwatch.android.data.model.PositiveDiagnosisReport
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
import org.covidwatch.android.ui.BaseViewModel

class PositiveDiagnosesViewModel(
    private val positiveDiagnosisRepository: PositiveDiagnosisRepository
) : BaseViewModel() {

    val positiveDiagnoses
        get() = positiveDiagnosisRepository.diagnoses()
            .map { it.sortedByDescending { report -> report.reportDate.toEpochMilli() } }

    fun deleteDiagnosis(diagnosis: PositiveDiagnosisReport) {
        viewModelScope.launch {
            positiveDiagnosisRepository.delete(diagnosis)
        }
    }
}