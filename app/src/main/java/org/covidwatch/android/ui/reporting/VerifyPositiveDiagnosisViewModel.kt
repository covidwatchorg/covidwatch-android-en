package org.covidwatch.android.ui.reporting

import androidx.lifecycle.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.covidwatch.android.data.DiagnosisVerificationManager
import org.covidwatch.android.data.PositiveDiagnosisReport
import org.covidwatch.android.data.PositiveDiagnosisVerification
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
import org.covidwatch.android.domain.RemoveUnverifiedReportsUseCase
import org.covidwatch.android.domain.StartUploadDiagnosisKeysWorkUseCase
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.extension.launchUseCase
import org.covidwatch.android.extension.send
import org.covidwatch.android.extension.sendNullable
import org.covidwatch.android.ui.BaseViewModel
import org.covidwatch.android.ui.event.Event
import org.covidwatch.android.ui.event.NullableEvent
import org.covidwatch.android.ui.util.DateFormatter
import java.time.Instant

class VerifyPositiveDiagnosisViewModel(
    private val state: SavedStateHandle,
    private val startUploadDiagnosisKeysWorkUseCase: StartUploadDiagnosisKeysWorkUseCase,
    private val removeUnverifiedReportsUseCase: RemoveUnverifiedReportsUseCase,
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

    private var positiveDiagnosisReport = PositiveDiagnosisReport()

    private val _showThankYou = MutableLiveData<Event<Unit>>()
    val showThankYou: LiveData<Event<Unit>> = _showThankYou

    val readyToSubmit: LiveData<Boolean> = diagnosisVerification.map { it?.readyToSubmit ?: false }

    private val _uploading = MutableLiveData<Boolean>()
    val uploading: LiveData<Boolean> = _uploading

    private val _infectionDateFormatted = MutableLiveData<String>()
    val infectionDateFormatted: LiveData<String> = _infectionDateFormatted

    private val _testDateFormatted = MutableLiveData<String>()
    val testDateFormatted: LiveData<String> = _testDateFormatted

    private val _symptomDateFormatted = MutableLiveData<String>()
    val symptomDateDateFormatted: LiveData<String> = _symptomDateFormatted

    private val _selectSymptomsDate = MutableLiveData<NullableEvent<Long?>>()
    val selectSymptomsDate: LiveData<NullableEvent<Long?>> = _selectSymptomsDate

    private val _selectInfectionDate = MutableLiveData<NullableEvent<Long?>>()
    val selectInfectionDate: LiveData<NullableEvent<Long?>> = _selectInfectionDate

    private val _selectTestDate = MutableLiveData<NullableEvent<Long?>>()
    val selectTestDate: LiveData<NullableEvent<Long?>> = _selectTestDate

    private var infectionDate: Instant?
        get() = state[STATE_INFECTION_DATE]
        set(value) {
            state[STATE_INFECTION_DATE] = value
        }

    private var testDate: Instant?
        get() = state[STATE_TEST_DATE]
        set(value) {
            state[STATE_TEST_DATE] = value
        }

    private var symptomDate: Instant?
        get() = state[STATE_SYMPTOM_DATE]
        set(value) {
            state[STATE_SYMPTOM_DATE] = value
        }

    fun selectSymptomsDate() = _selectSymptomsDate.sendNullable(symptomDate?.toEpochMilli())

    fun selectInfectionDate() = _selectInfectionDate.sendNullable(infectionDate?.toEpochMilli())

    fun selectTestDate() = _selectTestDate.sendNullable(testDate?.toEpochMilli())

    fun symptomDate(date: Long) {
        symptomDate = Instant.ofEpochMilli(date)
        _symptomDateFormatted.value = DateFormatter.format(date)
        setDiagnosisVerification(diagnosisVerification.value?.copy(symptomsStartDate = symptomDate))
    }

    fun testDate(date: Long) {
        testDate = Instant.ofEpochMilli(date)
        _testDateFormatted.value = DateFormatter.format(date)
        setDiagnosisVerification(diagnosisVerification.value?.copy(testDate = testDate))
    }

    fun infectionDate(date: Long) {
        infectionDate = Instant.ofEpochMilli(date)
        _infectionDateFormatted.value = DateFormatter.format(date)
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
            _uploading.value = true

            val code = diagnosisVerification.value?.verificationTestCode ?: ""

            // Check if we have verified report with this code in order to reuse token
            val diagnosis = positiveDiagnosisRepository.diagnosisByVerificationCode(code)
            val verificationData = diagnosis?.verificationData
            val token = verificationData?.token
            val testType = verificationData?.testType

            if (!token.isNullOrEmpty() && !testType.isNullOrEmpty()) {
                positiveDiagnosisReport = diagnosis

                val symptomsStartDate = verificationData.symptomsStartDate
                val certificate = verificationData.verificationCertificate
                val hmacKey = verificationData.hmacKey

                setDiagnosisVerification(
                    diagnosisVerification.value?.copy(
                        testType = testType,
                        symptomsStartDate = validSymptomsDate(symptomsStartDate),
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
                                symptomsStartDate = validSymptomsDate(it.symptomDate),
                                token = it.token
                            )
                        )

                        // Save just verified code in order to reuse token
                        positiveDiagnosisReport = positiveDiagnosisReport.copy(
                            verificationData = diagnosisVerification.value
                        )
                        positiveDiagnosisRepository.addPositiveDiagnosisReport(
                            positiveDiagnosisReport
                        )

                        shareReportIfEnEnabled()
                    }

                    failure {
                        _uploading.value = false
                        handleStatus(it)
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        GlobalScope.launchUseCase(removeUnverifiedReportsUseCase)
    }

    /**
     *  Take first the server data then consider user's input if "no symptoms" was not selected
     */
    private fun validSymptomsDate(serverSymptomDate: Instant?) = serverSymptomDate
        ?: symptomDate.takeIf { diagnosisVerification.value?.noSymptoms == false }

    private suspend fun shareReportIfEnEnabled() {
        enManager.isEnabled().apply {
            success { enabled ->
                if (enabled) {
                    shareReport()
                } else {
                    _uploading.value = false
                    withPermission(ExposureNotificationManager.PERMISSION_START_REQUEST_CODE) {
                        enManager.start().apply {
                            success { shareReport() }
                            failure { handleStatus(it) }
                        }
                    }
                }
            }

            failure {
                _uploading.value = false
                handleStatus(it)
            }
        }
    }

    private suspend fun shareReport() {
        withPermission(ExposureNotificationManager.PERMISSION_KEYS_REQUEST_CODE) {
            enManager.temporaryExposureKeyHistory().apply {
                _uploading.value = false

                success {
                    _uploading.value = true
                    observeStatus(
                        startUploadDiagnosisKeysWorkUseCase,
                        StartUploadDiagnosisKeysWorkUseCase.Params(
                            it,
                            positiveDiagnosisReport.copy(
                                verificationData = diagnosisVerification.value
                            )
                        )
                    ) { uploading ->
                        _uploading.value = false
                        uploading.success {
                            _showThankYou.send()
                        }
                    }
                }
                failure {
                    handleStatus(it)
                }
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