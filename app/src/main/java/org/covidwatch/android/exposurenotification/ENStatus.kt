package org.covidwatch.android.exposurenotification

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationClient
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationStatusCodes.*
import java.io.IOException

// TODO: 07.06.2020 Probably rename it to Failure and use for other failures in the app not related
// Exposure Notification framework
sealed class ENStatus(val code: Int) {
    object FailedRejectedOptIn : ENStatus(FAILED_REJECTED_OPT_IN)
    object FailedServiceDisabled : ENStatus(FAILED_SERVICE_DISABLED)
    object FailedBluetoothScanningDisabled : ENStatus(FAILED_BLUETOOTH_DISABLED)
    object FailedTemporarilyDisabled : ENStatus(FAILED_TEMPORARILY_DISABLED)

    // TODO: 07.06.2020 Rework this status because it's not about storage but IO errors
    // Like no file or can't read a file
    object FailedInsufficientStorage : ENStatus(FAILED_DISK_IO)
    object NetworkError : ENStatus(NETWORK_ERROR)

    object Failed : ENStatus(FAILED)

    class NeedsResolution(val exception: ApiException? = null) : ENStatus(RESOLUTION_REQUIRED)

    companion object {

        /**
         * Create [ENStatus] from [ApiException] when [ExposureNotificationClient] methods are called
         * */
        operator fun invoke(apiException: ApiException?) = when (apiException?.statusCode) {
            FAILED_REJECTED_OPT_IN -> FailedRejectedOptIn
            FAILED_SERVICE_DISABLED -> FailedServiceDisabled
            FAILED_BLUETOOTH_DISABLED -> FailedBluetoothScanningDisabled
            FAILED_TEMPORARILY_DISABLED -> FailedTemporarilyDisabled
            FAILED_DISK_IO -> FailedInsufficientStorage
            RESOLUTION_REQUIRED -> NeedsResolution(apiException)
            NETWORK_ERROR -> NetworkError
            FAILED -> Failed
            else -> Failed
        }

        /**
         * Generate [ENStatus] from [ApiException.getStatusCode]
         * */
        operator fun invoke(statusCode: Int?) = when (statusCode) {
            FAILED_REJECTED_OPT_IN -> FailedRejectedOptIn
            FAILED_SERVICE_DISABLED -> FailedServiceDisabled
            FAILED_BLUETOOTH_DISABLED -> FailedBluetoothScanningDisabled
            FAILED_TEMPORARILY_DISABLED -> FailedTemporarilyDisabled
            FAILED_DISK_IO -> FailedInsufficientStorage
            RESOLUTION_REQUIRED -> NeedsResolution()
            NETWORK_ERROR -> NetworkError
            FAILED -> Failed
            else -> Failed
        }
    }
}

class NoConnectionException : IOException() {
    override val message: String
        get() = "No internet available, please check your WIFi or Data connections"
}