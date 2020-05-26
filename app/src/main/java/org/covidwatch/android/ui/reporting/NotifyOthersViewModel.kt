package org.covidwatch.android.ui.reporting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import kotlinx.coroutines.GlobalScope
import org.covidwatch.android.data.PositiveDiagnosisReport
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
import org.covidwatch.android.domain.UploadDiagnosisKeysUseCase
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.extension.launchUseCase

class NotifyOthersViewModel(
    private val uploadDiagnosisKeysUseCase: UploadDiagnosisKeysUseCase,
    private val positiveDiagnosisRepository: PositiveDiagnosisRepository
) : ViewModel() {

    private val _uploadingReport = MutableLiveData<Boolean>()
    val uploadingReport: LiveData<Boolean> = _uploadingReport

    val positiveDiagnosis = positiveDiagnosisRepository.positiveDiagnosisReports().map {
        it.map { report ->
            val status = if (report.verified) TestStatus.Verified else TestStatus.NeedsVerification
            PositiveDiagnosisItem(status, report.reportDate)
        }
    }

    fun sharePositiveDiagnosis() {
        _uploadingReport.value = true

        // We need to make sure the upload will happen even if we close the screen
        // so we use GlobalScope for that
        GlobalScope.launchUseCase(uploadDiagnosisKeysUseCase) {
            // Hide uploading in any case
            _uploadingReport.value = false

            success {
                positiveDiagnosisRepository.addPositiveDiagnosisReport(
                    PositiveDiagnosisReport(
                        verified = true,
                        reportDate = System.currentTimeMillis()
                    )
                )
            }
            // Handle only failure case
            failure { handleError(it) }
        }
    }

    private fun handleError(status: ENStatus?) {
        when (status) {
            ENStatus.FailedRejectedOptIn -> TODO()
            ENStatus.FailedServiceDisabled -> TODO()
            ENStatus.FailedBluetoothScanningDisabled -> TODO()
            ENStatus.FailedTemporarilyDisabled -> TODO()
            ENStatus.FailedInsufficientStorage -> TODO()
            ENStatus.FailedInternal -> TODO()
        }
    }
}