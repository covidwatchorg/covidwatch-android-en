package org.covidwatch.android.exposurenotification

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationClient
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationStatusCodes.*
import java.io.IOException


sealed class Failure(val code: Int) {
    sealed class EnStatus {
        object Failed : Failure(FAILED)
        object AlreadyStarted : Failure(FAILED_ALREADY_STARTED)
        object NotSupported : Failure(FAILED_NOT_SUPPORTED)
        object RejectedOptIn : Failure(FAILED_REJECTED_OPT_IN)
        object ServiceDisabled : Failure(FAILED_SERVICE_DISABLED)
        object BluetoothDisabled : Failure(FAILED_BLUETOOTH_DISABLED)
        object TemporarilyDisabled : Failure(FAILED_TEMPORARILY_DISABLED)
        object FailedDiskIO : Failure(FAILED_DISK_IO)
        object Unauthorized : Failure(FAILED_UNAUTHORIZED)
        object RateLimited : Failure(FAILED_RATE_LIMITED)
        class NeedsResolution(val exception: ApiException? = null) : Failure(RESOLUTION_REQUIRED)
    }

    object NetworkError : Failure(NETWORK_ERROR)
    object ServerError : Failure(REMOTE_EXCEPTION)
    object DeviceAttestation : Failure(FAILED_DEVICE_ATTESTATION)

    companion object {
        const val FAILED_DEVICE_ATTESTATION = 444

        /**
         * Create [Failure] from [ApiException] when [ExposureNotificationClient] methods are called
         * */
        operator fun invoke(exception: Exception?) = when (exception) {
            is ApiException -> {
                when (exception.statusCode) {
                    FAILED_REJECTED_OPT_IN -> EnStatus.RejectedOptIn
                    FAILED_SERVICE_DISABLED -> EnStatus.ServiceDisabled
                    FAILED_BLUETOOTH_DISABLED -> EnStatus.BluetoothDisabled
                    FAILED_TEMPORARILY_DISABLED -> EnStatus.TemporarilyDisabled
                    FAILED_DISK_IO -> EnStatus.FailedDiskIO
                    RESOLUTION_REQUIRED -> EnStatus.NeedsResolution(exception)
                    NETWORK_ERROR -> NetworkError
                    REMOTE_EXCEPTION -> ServerError
                    FAILED -> EnStatus.Failed
                    else -> EnStatus.Failed
                }
            }
            is NoConnectionException -> NetworkError
            is ServerException -> ServerError
            else -> EnStatus.Failed
        }

        /**
         * Generate [Failure] from [ApiException.getStatusCode]
         * */
        operator fun invoke(statusCode: Int?) = when (statusCode) {
            FAILED_REJECTED_OPT_IN -> EnStatus.RejectedOptIn
            FAILED_SERVICE_DISABLED -> EnStatus.ServiceDisabled
            FAILED_BLUETOOTH_DISABLED -> EnStatus.BluetoothDisabled
            FAILED_TEMPORARILY_DISABLED -> EnStatus.TemporarilyDisabled
            FAILED_DISK_IO -> EnStatus.FailedDiskIO
            RESOLUTION_REQUIRED -> EnStatus.NeedsResolution()
            FAILED_DEVICE_ATTESTATION -> DeviceAttestation
            NETWORK_ERROR -> NetworkError
            REMOTE_EXCEPTION -> ServerError
            FAILED -> EnStatus.Failed
            else -> EnStatus.Failed
        }
    }
}

// TODO: 29.07.2020 Localization problem if we use the message in the UI
class NoConnectionException :
    IOException("No internet available, please check your WIFi or Data connections")

class ServerException(error: String? = null) : IOException(error)