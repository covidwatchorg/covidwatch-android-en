package org.covidwatch.android.ui.reporting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
import org.covidwatch.android.domain.StartUploadDiagnosisKeysWorkUseCase
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.functional.Either

class NotifyOthersViewModel(
    private val startUploadDiagnosisKeysWorkUseCase: StartUploadDiagnosisKeysWorkUseCase,
    positiveDiagnosisRepository: PositiveDiagnosisRepository
) : ViewModel() {

    private val _uploadDiagnosisKeys = MediatorLiveData<Either<ENStatus, Unit>>()
    val uploadDiagnosisKeys: LiveData<Either<ENStatus, Unit>> = _uploadDiagnosisKeys

    val positiveDiagnosis = positiveDiagnosisRepository.positiveDiagnosisReports().map {
        it.map { report ->
            val status = if (report.verified) TestStatus.Verified else TestStatus.NeedsVerification
            PositiveDiagnosisItem(status, report.reportDate)
        }
    }

    fun sharePositiveDiagnosis() {
        val uploadKeys = startUploadDiagnosisKeysWorkUseCase.observe()
        _uploadDiagnosisKeys.addSource(uploadKeys) {
            _uploadDiagnosisKeys.value = it
            _uploadDiagnosisKeys.removeSource(uploadKeys)
        }
    }
}