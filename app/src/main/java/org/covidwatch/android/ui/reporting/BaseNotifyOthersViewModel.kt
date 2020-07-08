package org.covidwatch.android.ui.reporting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
import org.covidwatch.android.extension.send
import org.covidwatch.android.ui.BaseViewModel
import org.covidwatch.android.ui.event.Event

open class BaseNotifyOthersViewModel(
    positiveDiagnosisRepository: PositiveDiagnosisRepository
) : BaseViewModel() {

    private val _openVerificationScreen = MutableLiveData<Event<Unit>>()
    val openVerificationScreen: LiveData<Event<Unit>> = _openVerificationScreen

    val positiveDiagnosis = positiveDiagnosisRepository.positiveDiagnosisReports().map {
        it.map { report ->
            val status = if (report.verified) TestStatus.Verified else TestStatus.NeedsVerification
            PositiveDiagnosisItem(status, report.reportDate)
        }
    }

    open fun sharePositiveDiagnosis() {
        _openVerificationScreen.send()
    }
}