package org.covidwatch.android.extension

import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.WorkInfo
import androidx.work.WorkManager
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.functional.Either
import java.util.*

const val FAILURE = "status"
const val UNKNOWN_FAILURE = -1

fun WorkInfo?.toResult() = when (this?.state) {
    WorkInfo.State.SUCCEEDED -> Either.Right(id)
    else -> {
        val code = this?.outputData?.getInt(FAILURE, UNKNOWN_FAILURE)
        val status = code?.let { ENStatus(it) } ?: ENStatus.Failed
        Either.Left(status)
    }
}

fun WorkManager.getFinalWorkInfoByIdLiveData(@NonNull id: UUID): LiveData<Either<ENStatus, UUID>> {
    val work = getWorkInfoByIdLiveData(id)
    val result = MediatorLiveData<Either<ENStatus, UUID>>()
    result.addSource(work) { workInfo ->
        @Suppress("NON_EXHAUSTIVE_WHEN")
        when (workInfo?.state) {
            null,
            WorkInfo.State.SUCCEEDED,
            WorkInfo.State.FAILED,
            WorkInfo.State.BLOCKED,
            WorkInfo.State.CANCELLED -> result.value = workInfo?.toResult()
        }
    }
    return result
}

fun failure(status: ENStatus) = ListenableWorker.Result.failure(
    Data.Builder().putInt(FAILURE, status.code).build()
)
