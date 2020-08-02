package org.covidwatch.android.ui.reporting

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.covidwatch.android.data.DiagnosisVerificationManager
import org.covidwatch.android.data.PositiveDiagnosisReport
import org.covidwatch.android.data.PositiveDiagnosisVerification
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
import org.covidwatch.android.domain.StartUploadDiagnosisKeysWorkUseCase
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.extension.send
import org.covidwatch.android.ui.BaseViewModel
import org.covidwatch.android.ui.event.Event
import java.util.*

class VerifyPositiveDiagnosisViewModel(
    private val state: SavedStateHandle,
    private val startUploadDiagnosisKeysWorkUseCase: StartUploadDiagnosisKeysWorkUseCase,
    private val verificationManager: DiagnosisVerificationManager,
    private val positiveDiagnosisRepository: PositiveDiagnosisRepository,
    private val enManager: ExposureNotificationManager
) : BaseViewModel() {

    private val diagnosisVerification =
        state.getLiveData<PositiveDiagnosisVerification>(STATE_DIAGNOSIS_VERIFICATION).also {
            if (it.value == null) {
                it.value = PositiveDiagnosisVerification()
            }
        }

    private val _showThankYou = MutableLiveData<Event<Unit>>()
    val showThankYou: LiveData<Event<Unit>> = _showThankYou

    val readyToSubmit: LiveData<Boolean> = diagnosisVerification.map { it?.readyToSubmit ?: false }

    private var infectionDate: Date?
        get() = state[STATE_INFECTION_DATE]
        set(value) {
            state[STATE_INFECTION_DATE] = value
        }

    private var testDate: Date?
        get() = state[STATE_TEST_DATE]
        set(value) {
            state[STATE_TEST_DATE] = value
        }

    private var symptomDate: Date?
        get() = state[STATE_SYMPTOM_DATE]
        set(value) {
            state[STATE_SYMPTOM_DATE] = value
        }

    fun symptomDate(date: Long) {
        symptomDate = Date(date)
        setDiagnosisVerification(diagnosisVerification.value?.copy(symptomsStartDate = symptomDate))
    }

    fun testDate(date: Long) {
        testDate = Date(date)
        setDiagnosisVerification(diagnosisVerification.value?.copy(testDate = testDate))
    }

    fun infectionDate(date: Long) {
        infectionDate = Date(date)
        setDiagnosisVerification(diagnosisVerification.value?.copy(possibleInfectionDate = infectionDate))
    }

    fun verificationCode(code: String) {
        setDiagnosisVerification(diagnosisVerification.value?.copy(verificationTestCode = code))
    }

    fun noSymptoms(noSymptoms: Boolean) {
        setDiagnosisVerification(
            diagnosisVerification.value?.copy(
                symptomsStartDate = if (noSymptoms) null else symptomDate,
                noSymptoms = noSymptoms
            )
        )
    }

    fun noInfectionDate(noInfectionDate: Boolean) {
        setDiagnosisVerification(
            diagnosisVerification.value?.copy(
                possibleInfectionDate = if (noInfectionDate) null else infectionDate,
                noInfectionDate = noInfectionDate
            )
        )
    }

    private fun setDiagnosisVerification(data: PositiveDiagnosisVerification?) {
        state[STATE_DIAGNOSIS_VERIFICATION] = data
        diagnosisVerification.value = data
    }

    fun sharePositiveDiagnosis() {
        viewModelScope.launch {
            val code = diagnosisVerification.value?.verificationTestCode ?: ""

            // Check if we have verified report with this code in order to reuse token
            val diagnosis = positiveDiagnosisRepository.diagnosisByVerificationCode(code)
            val verificationData = diagnosis?.verificationData
            val token = verificationData?.token
            val symptomsStartDate = verificationData?.symptomsStartDate
            val testType = verificationData?.testType
            val certificate = verificationData?.verificationCertificate
            val hmacKey = verificationData?.hmacKey

            if (!token.isNullOrEmpty() && !testType.isNullOrEmpty()) {
                setDiagnosisVerification(
                    diagnosisVerification.value?.copy(
                        testType = testType,
                        symptomsStartDate = symptomsStartDate ?: symptomDate,
                        token = token,
                        verificationCertificate = certificate,
                        hmacKey = hmacKey
                    )
                )

                shareReportIfEnEnabled()
            } else // Verify the code remotely if there is no local report with this code
                verificationManager.verify(code).apply {
                    success {
                        setDiagnosisVerification(
                            diagnosisVerification.value?.copy(
                                testType = it.testType,
                                symptomsStartDate = it.symptomDate ?: symptomDate,
                                token = it.token
                            )
                        )

                        // Save just verified code in order to reuse token
                        positiveDiagnosisRepository.addPositiveDiagnosisReport(
                            PositiveDiagnosisReport(
                                verificationData = diagnosisVerification.value
                            )
                        )

                        shareReportIfEnEnabled()
                    }

                    failure { handleStatus(it) }
                }
        }
    }

    private suspend fun shareReportIfEnEnabled() {
        enManager.isEnabled().apply {
            success { enabled ->
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

            failure { handleStatus(it) }
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

    companion object {
        private const val STATE_DIAGNOSIS_VERIFICATION = "diagnosis_verification"
        private const val STATE_INFECTION_DATE = "infection_date"
        private const val STATE_TEST_DATE = "test_date"
        private const val STATE_SYMPTOM_DATE = "symptom_date"
    }
}