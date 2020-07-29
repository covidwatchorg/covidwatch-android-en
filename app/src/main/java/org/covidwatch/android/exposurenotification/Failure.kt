package org.covidwatch.android.exposurenotification

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationClient
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationStatusCodes.*
import java.io.IOException

// TODO: 07.06.2020 Probably rename it to Failure and use for other failures in the app not related
// Exposure Notification framework
sealed class Failure(val code: Int) {

    object FailedRejectedOptIn : Failure(FAILED_REJECTED_OPT_IN)
    object FailedServiceDisabled : Failure(FAILED_SERVICE_DISABLED)
    object FailedBluetoothScanningDisabled : Failure(FAILED_BLUETOOTH_DISABLED)
    object FailedTemporarilyDisabled : Failure(FAILED_TEMPORARILY_DISABLED)

    // TODO: 07.06.2020 Rework this status because it's not about storage but IO errors
    // Like no file or can't read a file
    object FailedInsufficientStorage : Failure(FAILED_DISK_IO)
    object FailedDeviceAttestation : Failure(FAILED_DEVICE_ATTESTATION)
    object NetworkError : Failure(NETWORK_ERROR)
    object ServerError : Failure(REMOTE_EXCEPTION)

    object Failed : Failure(FAILED)

    class NeedsResolution(val exception: ApiException? = null) : Failure(RESOLUTION_REQUIRED)

    companion object {
        const val FAILED_DEVICE_ATTESTATION = 444

        /**
         * Create [Failure] from [ApiException] when [ExposureNotificationClient] methods are called
         * */
        operator fun invoke(exception: Exception?) = when (exception) {
            is ApiException -> {
                when (exception.statusCode) {
                    FAILED_REJECTED_OPT_IN -> FailedRejectedOptIn
                    FAILED_SERVICE_DISABLED -> FailedServiceDisabled
                    FAILED_BLUETOOTH_DISABLED -> FailedBluetoothScanningDisabled
                    FAILED_TEMPORARILY_DISABLED -> FailedTemporarilyDisabled
                    FAILED_DISK_IO -> FailedInsufficientStorage
                    RESOLUTION_REQUIRED -> NeedsResolution(exception)
                    NETWORK_ERROR -> NetworkError
                    REMOTE_EXCEPTION -> ServerError
                    FAILED -> Failed
                    else -> Failed
                }
            }
            is NoConnectionException -> NetworkError
            is ServerException -> ServerError
            else -> Failed
        }

        /**
         * Generate [Failure] from [ApiException.getStatusCode]
         * */
        operator fun invoke(statusCode: Int?) = when (statusCode) {
            FAILED_REJECTED_OPT_IN -> FailedRejectedOptIn
            FAILED_SERVICE_DISABLED -> FailedServiceDisabled
            FAILED_BLUETOOTH_DISABLED -> FailedBluetoothScanningDisabled
            FAILED_TEMPORARILY_DISABLED -> FailedTemporarilyDisabled
            FAILED_DISK_IO -> FailedInsufficientStorage
            RESOLUTION_REQUIRED -> NeedsResolution()
            FAILED_DEVICE_ATTESTATION -> FailedDeviceAttestation
            NETWORK_ERROR -> NetworkError
            REMOTE_EXCEPTION -> ServerError
            FAILED -> Failed
            else -> Failed
        }
    }
}

// TODO: 29.07.2020 Localization problem if we use the message in the UI
class NoConnectionException :
    IOException("No internet available, please check your WIFi or Data connections")

class ServerException(error: String? = null) : IOException(error)