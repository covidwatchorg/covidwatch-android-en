package org.covidwatch.android.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationClient
import kotlinx.coroutines.GlobalScope
import org.covidwatch.android.domain.UpdateExposureStateUseCase
import org.covidwatch.android.extension.launchUseCase
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber

class ExposureNotificationReceiver : BroadcastReceiver() {

    private val updateExposureStateUseCase by inject(
        UpdateExposureStateUseCase::class.java
    )

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.d("Receive a broadcast from Exposure Notification")
        val action = intent?.action
        if (action == ExposureNotificationClient.ACTION_EXPOSURE_STATE_UPDATED ||
            action == ExposureNotificationClient.ACTION_EXPOSURE_NOT_FOUND
        ) {
            val token = intent.getStringExtra(ExposureNotificationClient.EXTRA_TOKEN) ?: return
            Timber.d("Received broadcast is $action for token: $token")
            GlobalScope.launchUseCase(
                updateExposureStateUseCase,
                UpdateExposureStateUseCase.Params(token)
            )
        }
    }
}