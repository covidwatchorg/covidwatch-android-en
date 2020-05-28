package org.covidwatch.android.ui.exposurenotification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.covidwatch.android.data.CovidExposureInformation
import org.covidwatch.android.data.CovidExposureSummary
import org.covidwatch.android.data.exposureinformation.ExposureInformationRepository
import org.covidwatch.android.data.pref.PreferenceStorage
import org.covidwatch.android.domain.ProvideDiagnosisKeysUseCase
import org.covidwatch.android.domain.ProvideDiagnosisKeysUseCase.Params
import org.covidwatch.android.domain.UpdateExposureInformationUseCase
import org.covidwatch.android.domain.UploadDiagnosisKeysUseCase
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.extension.doOnNext
import org.covidwatch.android.extension.launchUseCase
import org.covidwatch.android.functional.Either
import org.covidwatch.android.ui.BaseViewModel

class ExposureNotificationViewModel(
    private val enManager: ExposureNotificationManager,
    private val uploadDiagnosisKeysUseCase: UploadDiagnosisKeysUseCase,
    private val provideDiagnosisKeysUseCase: ProvideDiagnosisKeysUseCase,
    private val updateExposureInformationUseCase: UpdateExposureInformationUseCase,
    exposureInformationRepository: ExposureInformationRepository,
    preferenceStorage: PreferenceStorage
) : BaseViewModel() {

    private val _exposureServiceRunning = MutableLiveData<Boolean>()
    val exposureServiceRunning: LiveData<Boolean> = _exposureServiceRunning

    val exposureInfo: LiveData<List<CovidExposureInformation>> =
        exposureInformationRepository.exposureInformation()

    val exposureSummary: LiveData<CovidExposureSummary> =
        preferenceStorage.observableExposureSummary.doOnNext {
            _showLoadButton.value = true
        }

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private val _showLoadButton = MutableLiveData<Boolean>()
    val showLoadButton: LiveData<Boolean> = _showLoadButton

    fun uploadDiagnosis() {
        viewModelScope.launchUseCase(uploadDiagnosisKeysUseCase) {
            failure { handleError(it) }
        }
    }

    fun startStopService() {
        viewModelScope.launch {
            // Check if we need to stop the service
            if (enManager.isEnabled().result() == true) {
                enManager.stop()
            } else { // Otherwise run the service
                enManager.start().result()
            }

            // Handle the case of is service running or not simply by asking the manager
            _exposureServiceRunning.value = enManager.isEnabled().result()
        }
    }

    fun downloadDiagnosisKeys() {
        observeStatus(provideDiagnosisKeysUseCase, Params(recurrent = false)) {
            _isRefreshing.value = false
        }
    }

    fun loadExposureInformation() {
        viewModelScope.launchUseCase(updateExposureInformationUseCase)
    }

    private fun <R : ENStatus, L> Either<R, L>.result(): L? {
        left?.let { handleError(it) }
        return right
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
