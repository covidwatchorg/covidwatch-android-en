package org.covidwatch.android.ui.reporting

import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
import org.covidwatch.android.exposurenotification.ExposureNotificationManager

class NotifyOthersFragment : BaseNotifyOthersFragment()

class NotifyOthersViewModel(
    positiveDiagnosisRepository: PositiveDiagnosisRepository
) : BaseNotifyOthersViewModel(
    positiveDiagnosisRepository
)