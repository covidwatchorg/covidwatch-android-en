package org.covidwatch.android.exposurenotification

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationClient
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationStatusCodes.*
import java.io.IOException


sealed class Failure(val code: Int, val message: String? = null) {
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

    data class ServerError(val error: String? = null) : Failure(SERVER_ERROR, error)
    data class CodeVerification(val error: String? = null) :
        Failure(FAILED_CODE_VERIFICATION, error)

    data class Internal(val error: String? = null) : Failure(FAILED_INTERNAL, error)

    companion object {
        const val FAILED_CODE_VERIFICATION = 111
        const val FAILED_INTERNAL = 222
        const val SERVER_ERROR = 333

        /**
         * Create [Failure] from [ApiException] when [ExposureNotificationClient] methods are called
         * */
        operator fun invoke(exception: Exception?) = when (exception) {
            // TODO: 30.07.2020 How to get ConnectionResult object to get real status codes?
            is ApiException -> {
                when (exception.statusCode) {
                    FAILED -> EnStatus.Failed
                    FAILED_ALREADY_STARTED -> EnStatus.AlreadyStarted
                    FAILED_NOT_SUPPORTED -> EnStatus.NotSupported
                    FAILED_REJECTED_OPT_IN -> EnStatus.RejectedOptIn
                    FAILED_SERVICE_DISABLED -> EnStatus.ServiceDisabled
                    FAILED_BLUETOOTH_DISABLED -> EnStatus.BluetoothDisabled
                    FAILED_TEMPORARILY_DISABLED -> EnStatus.TemporarilyDisabled
                    FAILED_DISK_IO -> EnStatus.FailedDiskIO
                    FAILED_UNAUTHORIZED -> EnStatus.Unauthorized
                    FAILED_RATE_LIMITED -> EnStatus.RateLimited
                    RESOLUTION_REQUIRED -> EnStatus.NeedsResolution(exception)
                    // Old version of play services can't return meaningful status code so we should
                    // parse if one the unknown status code are from Exposure Notifications
                    API_NOT_CONNECTED -> {
                        val statusCode = when {
                            exception.hasCode(FAILED_NOT_SUPPORTED) -> FAILED_NOT_SUPPORTED
                            exception.hasCode(FAILED_UNAUTHORIZED) -> FAILED_UNAUTHORIZED
                            else -> FAILED
                        }

                        invoke(statusCode)
                    }
                    NETWORK_ERROR -> NetworkError
                    REMOTE_EXCEPTION -> ServerError(exception.status.statusMessage)
                    else -> EnStatus.Failed
                }
            }
            is NoConnectionException -> NetworkError
            is ServerException -> ServerError(exception.error)
            else -> EnStatus.Failed
        }

        private fun ApiException.hasCode(
            code: Int
        ) = this.status.statusMessage?.contains(code.toString()) == true

        /**
         * Generate [Failure] from [ApiException.getStatusCode]
         * */
        operator fun invoke(statusCode: Int?, message: String? = null) = when (statusCode) {
            FAILED -> EnStatus.Failed
            FAILED_ALREADY_STARTED -> EnStatus.AlreadyStarted
            FAILED_NOT_SUPPORTED -> EnStatus.NotSupported
            FAILED_REJECTED_OPT_IN -> EnStatus.RejectedOptIn
            FAILED_SERVICE_DISABLED -> EnStatus.ServiceDisabled
            FAILED_BLUETOOTH_DISABLED -> EnStatus.BluetoothDisabled
            FAILED_TEMPORARILY_DISABLED -> EnStatus.TemporarilyDisabled
            FAILED_DISK_IO -> EnStatus.FailedDiskIO
            FAILED_UNAUTHORIZED -> EnStatus.Unauthorized
            FAILED_RATE_LIMITED -> EnStatus.RateLimited
            NETWORK_ERROR -> NetworkError
            SERVER_ERROR,
            REMOTE_EXCEPTION -> ServerError(message)
            FAILED_CODE_VERIFICATION -> CodeVerification(message)
            FAILED_INTERNAL -> Internal(message)
            else -> EnStatus.Failed
        }
    }
}

class NoConnectionException :
    IOException("No internet available, please check your WIFi or Data connections")

data class ServerException(val error: String? = null) : IOException(error)