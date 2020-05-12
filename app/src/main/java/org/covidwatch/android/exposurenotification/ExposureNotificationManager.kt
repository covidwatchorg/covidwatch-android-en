package org.covidwatch.android.exposurenotification

import com.google.android.gms.nearby.exposurenotification.ExposureConfiguration
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationClient
import com.google.common.io.BaseEncoding
import org.covidwatch.android.extension.await
import org.covidwatch.android.extension.awaitWithStatus
import java.io.File
import java.security.SecureRandom

class ExposureNotificationManager(
    private val exposureNotification: ExposureNotificationClient
) {
    private val base64 = BaseEncoding.base64()
    private val randomTokenByteLength = 32
    private val secureRandom: SecureRandom = SecureRandom()

    private val token: String = "temp_token"

    private val randomToken: String
        get() {
            val bytes = ByteArray(randomTokenByteLength)
            secureRandom.nextBytes(bytes)
            return base64.encode(bytes)
        }

    /* API */
    suspend fun start() = exposureNotification.start().awaitWithStatus()

    suspend fun temporaryExposureKeyHistory() =
        exposureNotification.temporaryExposureKeyHistory.awaitWithStatus()

    suspend fun getExposureInformation() =
        exposureNotification.getExposureInformation(token).awaitWithStatus()

    suspend fun stop() = exposureNotification.stop().awaitWithStatus()

    suspend fun isEnabled() = exposureNotification.isEnabled.awaitWithStatus()

    suspend fun provideDiagnosisKeys(keys: List<File>, token: String) =
        exposureNotification.provideDiagnosisKeys(
            keys,
            //TODO: Use a proper configuration source
            ExposureConfiguration.ExposureConfigurationBuilder().build(),
            token
        ).await()

    suspend fun getExposureSummary(token: String) =
        exposureNotification.getExposureSummary(token).await()
}