package org.covidwatch.android.exposurenotification

import com.google.android.gms.nearby.exposurenotification.ExposureConfiguration
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationClient
import org.covidwatch.android.extension.await
import org.covidwatch.android.extension.awaitNoResult
import org.covidwatch.android.extension.awaitWithStatus
import java.io.File

class ExposureNotificationManager(
    private val exposureNotification: ExposureNotificationClient
) {
    /* API */
    suspend fun start() = exposureNotification.start().awaitNoResult()

    suspend fun temporaryExposureKeyHistory() =
        exposureNotification.temporaryExposureKeyHistory.awaitWithStatus()

    suspend fun getExposureInformation(token: String) =
        exposureNotification.getExposureInformation(token).awaitWithStatus()

    suspend fun stop() = exposureNotification.stop().awaitNoResult()

    suspend fun isEnabled() = exposureNotification.isEnabled.awaitWithStatus()

    suspend fun provideDiagnosisKeys(keys: List<File>, token: String) =
        exposureNotification.provideDiagnosisKeys(
            keys,
            //TODO: Use a proper configuration source
            ExposureConfiguration.ExposureConfigurationBuilder().build(),
            token
        ).awaitNoResult()

    suspend fun getExposureSummary(token: String) =
        exposureNotification.getExposureSummary(token).await()

    companion object {
        const val PERMISSION_START_REQUEST_CODE = 100
        const val PERMISSION_KEYS_REQUEST_CODE = 200
    }
}