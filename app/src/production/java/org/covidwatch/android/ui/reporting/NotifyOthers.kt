package org.covidwatch.android.ui.reporting

import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
import org.covidwatch.android.domain.StartUploadDiagnosisKeysWorkUseCase
import org.covidwatch.android.exposurenotification.ExposureNotificationManager

class NotifyOthersFragment : BaseNotifyOthersFragment()

class NotifyOthersViewModel(
    startUploadDiagnosisKeysWorkUseCase: StartUploadDiagnosisKeysWorkUseCase,
    enManager: ExposureNotificationManager,
    positiveDiagnosisRepository: PositiveDiagnosisRepository
) : BaseNotifyOthersViewModel(
    startUploadDiagnosisKeysWorkUseCase,
    enManager,
    positiveDiagnosisRepository
)