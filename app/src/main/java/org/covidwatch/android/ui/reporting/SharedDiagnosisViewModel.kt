package org.covidwatch.android.ui.reporting

import androidx.lifecycle.map
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
import org.covidwatch.android.ui.BaseViewModel

open class SharedDiagnosisViewModel(
    positiveDiagnosisRepository: PositiveDiagnosisRepository
) : BaseViewModel() {

    val positiveDiagnosis = positiveDiagnosisRepository.positiveDiagnosisReports().map {
        it.map { report ->
            val status = if (report.verified) TestStatus.Verified else TestStatus.NeedsVerification
            PositiveDiagnosisItem(status, report.reportDate)
        }
    }
}