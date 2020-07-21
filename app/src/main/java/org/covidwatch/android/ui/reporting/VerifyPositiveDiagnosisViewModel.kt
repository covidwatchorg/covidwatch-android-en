package org.covidwatch.android.ui.reporting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.covidwatch.android.data.PositiveDiagnosisReport
import org.covidwatch.android.data.PositiveDiagnosisVerification
import org.covidwatch.android.domain.StartUploadDiagnosisKeysWorkUseCase
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.extension.mutableLiveData
import org.covidwatch.android.extension.send
import org.covidwatch.android.ui.BaseViewModel
import org.covidwatch.android.ui.event.Event
import java.util.*

class VerifyPositiveDiagnosisViewModel(
    private val startUploadDiagnosisKeysWorkUseCase: StartUploadDiagnosisKeysWorkUseCase,
    private val enManager: ExposureNotificationManager
) : BaseViewModel() {

    private val diagnosisVerification = mutableLiveData(PositiveDiagnosisVerification())

    private val _showThankYou = MutableLiveData<Event<Unit>>()
    val showThankYou: LiveData<Event<Unit>> = _showThankYou

    val readyToSubmit: LiveData<Boolean> = diagnosisVerification.map { it?.readyToSubmit ?: false }

    fun symptomsStartDate(date: Long) {
        diagnosisVerification.value =
            diagnosisVerification.value?.copy(symptomsStartDate = Date(date))
    }

    fun testedDate(date: Long) {
        diagnosisVerification.value = diagnosisVerification.value?.copy(testDate = Date(date))
    }

    fun exposedDate(date: Long) {
        diagnosisVerification.value =
            diagnosisVerification.value?.copy(possibleInfectionDate = Date(date))
    }

    fun verificationCode(code: String) {
        diagnosisVerification.value = diagnosisVerification.value?.copy(verificationTestCode = code)
    }

    fun noSymptoms(noSymptoms: Boolean) {
        diagnosisVerification.value = diagnosisVerification.value?.copy(noSymptoms = noSymptoms)
    }

    fun noExposedDate(noExposedDate: Boolean) {
        diagnosisVerification.value =
            diagnosisVerification.value?.copy(noExposedDate = noExposedDate)
    }

    fun sharePositiveDiagnosis() {
        viewModelScope.launch {
            enManager.isEnabled().success { enabled ->
                if (enabled) {
                    shareReport()
                } else {
                    withPermission(ExposureNotificationManager.PERMISSION_START_REQUEST_CODE) {
                        enManager.start().apply {
                            success { shareReport() }
                            failure { handleStatus(it) }
                        }
                    }
                }
            }
        }
    }

    private suspend fun shareReport() {
        withPermission(ExposureNotificationManager.PERMISSION_KEYS_REQUEST_CODE) {
            enManager.temporaryExposureKeyHistory().apply {
                success {
                    observeStatus(
                        startUploadDiagnosisKeysWorkUseCase,
                        StartUploadDiagnosisKeysWorkUseCase.Params(
                            it,
                            PositiveDiagnosisReport(
                                verificationData = diagnosisVerification.value
                            )
                        )
                    ) { uploading ->
                        uploading.success {
                            _showThankYou.send()
                        }
                    }
                }
                failure { handleStatus(it) }
            }
        }
    }
}