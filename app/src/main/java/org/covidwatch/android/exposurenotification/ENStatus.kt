package org.covidwatch.android.exposurenotification

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationClient
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationStatusCodes.*

sealed class ENStatus(val code: Int) {
    object FailedRejectedOptIn : ENStatus(FAILED_REJECTED_OPT_IN)
    object FailedServiceDisabled : ENStatus(FAILED_SERVICE_DISABLED)
    object FailedBluetoothScanningDisabled : ENStatus(FAILED_BLUETOOTH_DISABLED)
    object FailedTemporarilyDisabled : ENStatus(FAILED_TEMPORARILY_DISABLED)
    object FailedInsufficientStorage : ENStatus(FAILED_DISK_IO)
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
            FAILED -> Failed
            else -> Failed
        }
    }
}