package org.covidwatch.android.exposurenotification

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationClient
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationStatusCodes.*
import java.io.IOException


sealed class Failure(val code: Int) {
    sealed class EnStatus {
        object FailedRejectedOptIn : Failure(FAILED_REJECTED_OPT_IN)
        object FailedServiceDisabled : Failure(FAILED_SERVICE_DISABLED)
        object FailedBluetoothScanningDisabled : Failure(FAILED_BLUETOOTH_DISABLED)
        object FailedTemporarilyDisabled : Failure(FAILED_TEMPORARILY_DISABLED)

        object FailedDiskIo : Failure(FAILED_DISK_IO)
        object FailedDeviceAttestation : Failure(FAILED_DEVICE_ATTESTATION)
        object Failed : Failure(FAILED)
        class NeedsResolution(val exception: ApiException? = null) : Failure(RESOLUTION_REQUIRED)
    }

    object NetworkError : Failure(NETWORK_ERROR)
    object ServerError : Failure(REMOTE_EXCEPTION)

    companion object {
        const val FAILED_DEVICE_ATTESTATION = 444

        /**
         * Create [Failure] from [ApiException] when [ExposureNotificationClient] methods are called
         * */
        operator fun invoke(exception: Exception?) = when (exception) {
            is ApiException -> {
                when (exception.statusCode) {
                    FAILED_REJECTED_OPT_IN -> EnStatus.FailedRejectedOptIn
                    FAILED_SERVICE_DISABLED -> EnStatus.FailedServiceDisabled
                    FAILED_BLUETOOTH_DISABLED -> EnStatus.FailedBluetoothScanningDisabled
                    FAILED_TEMPORARILY_DISABLED -> EnStatus.FailedTemporarilyDisabled
                    FAILED_DISK_IO -> EnStatus.FailedDiskIo
                    RESOLUTION_REQUIRED -> EnStatus.NeedsResolution(
                        exception
                    )
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
            FAILED_REJECTED_OPT_IN -> EnStatus.FailedRejectedOptIn
            FAILED_SERVICE_DISABLED -> EnStatus.FailedServiceDisabled
            FAILED_BLUETOOTH_DISABLED -> EnStatus.FailedBluetoothScanningDisabled
            FAILED_TEMPORARILY_DISABLED -> EnStatus.FailedTemporarilyDisabled
            FAILED_DISK_IO -> EnStatus.FailedDiskIo
            RESOLUTION_REQUIRED -> EnStatus.NeedsResolution()
            FAILED_DEVICE_ATTESTATION -> EnStatus.FailedDeviceAttestation
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