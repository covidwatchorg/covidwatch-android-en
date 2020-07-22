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

    private var infectionDate: Date? = null
    private var testDate: Date? = null
    private var symptomDate: Date? = null

    fun symptomDate(date: Long) {
        symptomDate = Date(date)
        diagnosisVerification.value =
            diagnosisVerification.value?.copy(symptomsStartDate = symptomDate)
    }

    fun testDate(date: Long) {
        testDate = Date(date)
        diagnosisVerification.value = diagnosisVerification.value?.copy(testDate = testDate)
    }

    fun infectionDate(date: Long) {
        infectionDate = Date(date)
        diagnosisVerification.value =
            diagnosisVerification.value?.copy(possibleInfectionDate = infectionDate)
    }

    fun verificationCode(code: String) {
        diagnosisVerification.value = diagnosisVerification.value?.copy(verificationTestCode = code)
    }

    fun noSymptoms(noSymptoms: Boolean) {
        diagnosisVerification.value = diagnosisVerification.value?.copy(
            symptomsStartDate = if (noSymptoms) null else symptomDate,
            noSymptoms = noSymptoms
        )
    }

    fun noInfectionDate(noInfectionDate: Boolean) {
        diagnosisVerification.value =
            diagnosisVerification.value?.copy(
                possibleInfectionDate = if (noInfectionDate) null else infectionDate,
                noInfectionDate = noInfectionDate
            )
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