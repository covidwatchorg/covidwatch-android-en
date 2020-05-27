package org.covidwatch.android.exposurenotification

import androidx.annotation.IntDef
import org.covidwatch.android.exposurenotification.Status.Companion.FAILED_BLUETOOTH_SCANNING_DISABLED
import org.covidwatch.android.exposurenotification.Status.Companion.FAILED_INSUFFICENT_STORAGE
import org.covidwatch.android.exposurenotification.Status.Companion.FAILED_INTERNAL
import org.covidwatch.android.exposurenotification.Status.Companion.FAILED_REJECTED_OPT_IN
import org.covidwatch.android.exposurenotification.Status.Companion.FAILED_SERVICE_DISABLED
import org.covidwatch.android.exposurenotification.Status.Companion.FAILED_TEMPORARILY_DISABLED
import java.io.Serializable

sealed class ENStatus(val code: Int) : Serializable {
    object FailedRejectedOptIn : ENStatus(FAILED_REJECTED_OPT_IN)
    object FailedServiceDisabled : ENStatus(FAILED_SERVICE_DISABLED)
    object FailedBluetoothScanningDisabled : ENStatus(FAILED_BLUETOOTH_SCANNING_DISABLED)
    object FailedTemporarilyDisabled : ENStatus(FAILED_TEMPORARILY_DISABLED)
    object FailedInsufficientStorage : ENStatus(FAILED_INSUFFICENT_STORAGE)
    object FailedInternal : ENStatus(FAILED_INTERNAL)
    companion object {
        operator fun invoke(@Status status: Int?) = when (status) {
            FAILED_REJECTED_OPT_IN -> FailedRejectedOptIn
            FAILED_SERVICE_DISABLED -> FailedServiceDisabled
            FAILED_BLUETOOTH_SCANNING_DISABLED -> FailedBluetoothScanningDisabled
            FAILED_TEMPORARILY_DISABLED -> FailedTemporarilyDisabled
            FAILED_INSUFFICENT_STORAGE -> FailedInsufficientStorage
            FAILED_INTERNAL -> FailedInternal
            else -> FailedInternal
        }
    }
}

//TODO: Replace with real status constants from Nearby when they are available
@IntDef
internal annotation class Status {
    companion object {
        var FAILED_REJECTED_OPT_IN = 1
        var FAILED_SERVICE_DISABLED = 2
        var FAILED_BLUETOOTH_SCANNING_DISABLED = 3
        var FAILED_TEMPORARILY_DISABLED = 4
        var FAILED_INSUFFICENT_STORAGE = 5
        var FAILED_INTERNAL = 6
    }
}