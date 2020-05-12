package org.covidwatch.android.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationClient
import kotlinx.coroutines.GlobalScope
import org.covidwatch.android.domain.UpdateExposureStateUseCase
import org.covidwatch.android.extension.launchUseCase
import org.koin.java.KoinJavaComponent.inject

class ExposureNotificationReceiver : BroadcastReceiver() {

    private val updateExposureStateUseCase by inject(
        UpdateExposureStateUseCase::class.java
    )

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ExposureNotificationClient.ACTION_EXPOSURE_STATE_UPDATED) {
            val token = intent.getStringExtra(ExposureNotificationClient.EXTRA_TOKEN)
            GlobalScope.launchUseCase(updateExposureStateUseCase)
        }
    }
}