package org.covidwatch.android.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationClient
import kotlinx.coroutines.GlobalScope
import org.covidwatch.android.domain.UpdateExposureStateUseCase
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.extension.io
import org.covidwatch.android.extension.launchUseCase
import org.covidwatch.android.ui.Notifications
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber

class ExposureNotificationReceiver : BroadcastReceiver() {

    private val updateExposureStateUseCase by inject(
        UpdateExposureStateUseCase::class.java
    )
    private val enManager by inject(ExposureNotificationManager::class.java)
    private val notifications by inject(Notifications::class.java)

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action

        Timber.d("Receive $action broadcast from Exposure Notification")
        when (action) {
            ExposureNotificationClient.ACTION_EXPOSURE_STATE_UPDATED -> GlobalScope.launchUseCase(
                updateExposureStateUseCase
            )
            ExposureNotificationClient.ACTION_EXPOSURE_NOT_FOUND -> Timber.d("No exposures")
            // TODO: 08/09/2020 This seems to be not working.
            // Check when full support if ExposureWindow is introduced
            // TODO: Change notifications channel for this messages
            ExposureNotificationClient.ACTION_SERVICE_STATE_UPDATED -> {
                GlobalScope.io {
                    enManager.isEnabled().apply {
                        success { notifications.postExposureNotificationsDisabled(it) }
                        failure { Timber.d("Something interesting ${it.code}") }
                    }
                }
            }
        }
    }
}