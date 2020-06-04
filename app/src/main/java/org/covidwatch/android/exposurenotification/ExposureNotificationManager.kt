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
            ExposureConfiguration.ExposureConfigurationBuilder()
                .setMinimumRiskScore(1)
                .setDurationAtAttenuationThresholds(58, 73)
                .setAttenuationScores(2, 5, 8, 8, 8, 8, 8, 8)
                .setDaysSinceLastExposureScores(1, 2, 2, 4, 6, 8, 8, 8)
                .setDurationScores(1, 1, 4, 7, 7, 8, 8, 8)
                .setTransmissionRiskScores(0, 3, 6, 8, 8, 6, 0, 6)
                .build(),
            token
        ).awaitNoResult()

    suspend fun getExposureSummary(token: String) =
        exposureNotification.getExposureSummary(token).await()

    companion object {
        const val PERMISSION_START_REQUEST_CODE = 100
        const val PERMISSION_KEYS_REQUEST_CODE = 200
    }
}