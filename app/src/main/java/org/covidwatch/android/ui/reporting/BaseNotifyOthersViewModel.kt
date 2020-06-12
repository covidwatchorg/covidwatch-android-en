package org.covidwatch.android.ui.reporting

import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.google.android.gms.nearby.exposurenotification.TemporaryExposureKey
import kotlinx.coroutines.launch
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
import org.covidwatch.android.domain.StartUploadDiagnosisKeysWorkUseCase
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.exposurenotification.ExposureNotificationManager.Companion.PERMISSION_KEYS_REQUEST_CODE
import org.covidwatch.android.exposurenotification.ExposureNotificationManager.Companion.PERMISSION_START_REQUEST_CODE
import org.covidwatch.android.ui.BaseViewModel

open class BaseNotifyOthersViewModel(
    protected val startUploadDiagnosisKeysWorkUseCase: StartUploadDiagnosisKeysWorkUseCase,
    private val enManager: ExposureNotificationManager,
    positiveDiagnosisRepository: PositiveDiagnosisRepository
) : BaseViewModel() {

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

    open fun onTekHistory(teks: MutableList<TemporaryExposureKey>) {
        observeStatus(startUploadDiagnosisKeysWorkUseCase)
    }

    private suspend fun shareReport() {
        withPermission(PERMISSION_KEYS_REQUEST_CODE) {
            enManager.temporaryExposureKeyHistory().apply {
                success { onTekHistory(it) }
                failure { handleStatus(it) }
            }
        }
    }
}