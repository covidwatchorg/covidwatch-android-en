package org.covidwatch.android.exposurenotification

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationStatusCodes.*
import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

@Serializable
sealed class ENStatus(val code: Int) {
    @Serializable
    object FailedRejectedOptIn : ENStatus(FAILED_REJECTED_OPT_IN)

    @Serializable
    object FailedServiceDisabled : ENStatus(FAILED_SERVICE_DISABLED)

    @Serializable
    object FailedBluetoothScanningDisabled : ENStatus(FAILED_BLUETOOTH_DISABLED)

    @Serializable
    object FailedTemporarilyDisabled : ENStatus(FAILED_TEMPORARILY_DISABLED)

    @Serializable
    object FailedInsufficientStorage : ENStatus(FAILED_DISK_IO)

    @Serializable
    object Failed : ENStatus(FAILED)

    @Serializable
    class NeedsResolution(@ContextualSerialization val exception: ApiException) :
        ENStatus(RESOLUTION_REQUIRED)

    fun toJson(): String {
        return Json(JsonConfiguration.Stable).stringify(serializer(), this)
    }

    companion object {

        fun fromJson(json: String): ENStatus {
            return Json(JsonConfiguration.Stable).parse(serializer(), json)
        }

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
    }
}