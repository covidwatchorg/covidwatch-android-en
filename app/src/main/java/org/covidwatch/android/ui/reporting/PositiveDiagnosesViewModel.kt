package org.covidwatch.android.ui.reporting

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.covidwatch.android.data.PositiveDiagnosisReport
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
import org.covidwatch.android.ui.BaseViewModel

class PositiveDiagnosesViewModel(
    private val positiveDiagnosisRepository: PositiveDiagnosisRepository
) : BaseViewModel() {

    val positiveDiagnoses
        get() = positiveDiagnosisRepository.diagnoses()

    fun deleteDiagnosis(diagnosis: PositiveDiagnosisReport) {
        viewModelScope.launch {
            positiveDiagnosisRepository.delete(diagnosis)
        }
    }
}