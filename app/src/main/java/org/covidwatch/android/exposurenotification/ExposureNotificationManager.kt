package org.covidwatch.android.exposurenotification

import com.google.android.gms.nearby.exposurenotification.DiagnosisKeysDataMapping
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

    suspend fun isDisabled(): Boolean {
        val enabled = exposureNotification.isEnabled.awaitWithStatus()
        return enabled.isLeft || enabled.right == false
    }

    @Deprecated("Deprecated ExposureInformation logic")
    suspend fun provideDiagnosisKeys(
        keys: List<File>,
        token: String,
        exposureConfiguration: ExposureConfiguration
    ) = exposureNotification.provideDiagnosisKeys(
        keys,
        exposureConfiguration,
        token
    ).awaitNoResult()

    suspend fun provideDiagnosisKeys(keys: List<File>) =
        exposureNotification.provideDiagnosisKeys(keys).awaitNoResult()

    suspend fun getExposureSummary(token: String) =
        exposureNotification.getExposureSummary(token).await()

    suspend fun diagnosisKeysDataMapping(mapping: DiagnosisKeysDataMapping) =
        exposureNotification.setDiagnosisKeysDataMapping(mapping).awaitWithStatus()

    companion object {
        const val PERMISSION_START_REQUEST_CODE = 100
        const val PERMISSION_KEYS_REQUEST_CODE = 200
    }
}