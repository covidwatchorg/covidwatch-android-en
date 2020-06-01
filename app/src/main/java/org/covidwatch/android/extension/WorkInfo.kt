package org.covidwatch.android.extension

import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.work.WorkInfo
import androidx.work.WorkManager
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.functional.Either
import java.util.*

const val FAILURE = "status"

fun WorkInfo.toResult() = when (this.state) {
    WorkInfo.State.SUCCEEDED -> Either.Right(id)
    else -> {
        val failure =
            outputData.getString(FAILURE)?.let { ENStatus.fromJson(it) } ?: ENStatus.Failed

        Either.Left(failure)
    }
}

fun WorkManager.getFinalWorkInfoByIdLiveData(@NonNull id: UUID): LiveData<Either<ENStatus, UUID>> {
    val work = getWorkInfoByIdLiveData(id)
    val result = MediatorLiveData<Either<ENStatus, UUID>>()
    result.addSource(work) { workInfo ->
        @Suppress("NON_EXHAUSTIVE_WHEN")
        when (workInfo.state) {
            WorkInfo.State.SUCCEEDED,
            WorkInfo.State.FAILED,
            WorkInfo.State.BLOCKED,
            WorkInfo.State.CANCELLED -> result.value = workInfo.toResult()
        }
    }
    return result
}