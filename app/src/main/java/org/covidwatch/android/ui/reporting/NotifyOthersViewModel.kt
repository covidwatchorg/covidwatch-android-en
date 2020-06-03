package org.covidwatch.android.ui.reporting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
import org.covidwatch.android.domain.ExportDiagnosisKeysAsFileUseCase
import org.covidwatch.android.domain.StartUploadDiagnosisKeysWorkUseCase
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.exposurenotification.ExposureNotificationManager.Companion.PERMISSION_KEYS_REQUEST_CODE
import org.covidwatch.android.exposurenotification.ExposureNotificationManager.Companion.PERMISSION_START_REQUEST_CODE
import org.covidwatch.android.extension.launchUseCase
import org.covidwatch.android.extension.send
import org.covidwatch.android.ui.BaseViewModel
import org.covidwatch.android.ui.event.Event
import java.io.File

class NotifyOthersViewModel(
    private val startUploadDiagnosisKeysWorkUseCase: StartUploadDiagnosisKeysWorkUseCase,
    private val exportDiagnosisKeysAsFileUseCase: ExportDiagnosisKeysAsFileUseCase,
    private val enManager: ExposureNotificationManager,
    positiveDiagnosisRepository: PositiveDiagnosisRepository
) : BaseViewModel() {

    private val riskLevelSeparator = " "

    private val riskLevels = mutableListOf<Int>()

    private val _shareDiagnosisFile = MutableLiveData<Event<List<File>>>()
    val shareDiagnosisFile: LiveData<Event<List<File>>> = _shareDiagnosisFile

    private val _setTransmissionLevelRisk = MutableLiveData<Event<List<Int>>>()
    val setTransmissionLevelRisk: LiveData<Event<List<Int>>> = _setTransmissionLevelRisk

    private val _chooseShareMethod = MutableLiveData<Event<Unit>>()
    val chooseShareMethod: LiveData<Event<Unit>> = _chooseShareMethod

    val positiveDiagnosis = positiveDiagnosisRepository.positiveDiagnosisReports().map {
        it.map { report ->
            val status = if (report.verified) TestStatus.Verified else TestStatus.NeedsVerification
            PositiveDiagnosisItem(status, report.reportDate)
        }
    }

    fun sharePositiveDiagnosis() {
        viewModelScope.launch {
            enManager.isEnabled().success { enabled ->
                if (enabled) {
                    shareReport()
                } else {
                    withPermission(PERMISSION_START_REQUEST_CODE) {
                        enManager.start().apply {
                            success { shareReport() }
                            failure { handleStatus(it) }
                        }
                    }
                }
            }
        }
    }

    fun riskLevelsAsString(riskLevels: List<Int>) =
        riskLevels.joinToString(separator = riskLevelSeparator)

    private fun stringToRiskLevels(riskLevels: String) =
        riskLevels.split(riskLevelSeparator).map { it.toInt() }

    fun shareReport(riskLevels: String) {
        this.riskLevels.clear()
        this.riskLevels.addAll(stringToRiskLevels(riskLevels))
        _chooseShareMethod.send()
    }

    fun shareReportAs(file: Boolean) {
        if (file) {
            viewModelScope.launchUseCase(exportDiagnosisKeysAsFileUseCase) {
                success { _shareDiagnosisFile.send(it) }
                failure { handleStatus(it) }
            }
        } else {
            observeStatus(startUploadDiagnosisKeysWorkUseCase)
        }
    }

    private suspend fun shareReport() {
        withPermission(PERMISSION_KEYS_REQUEST_CODE) {
            enManager.temporaryExposureKeyHistory().apply {
                success {
                    // TODO: 03.06.2020 Replace the magic 3 with dynamic values
                    _setTransmissionLevelRisk.send(it.map { 3 })
                }
                failure { handleStatus(it) }
            }
        }
    }
}