package org.covidwatch.android.extension

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.functional.Either
import timber.log.Timber

// TODO: 13.07.2020 Use proper adapter from Task to Coroutines
suspend fun <T> Task<T>.await(): Either<ApiException?, T> = withContext(Dispatchers.IO) {
    try {
        Either.Right(Tasks.await(this@await))
    } catch (e: Exception) {
        val apiException = e.cause as? ApiException
        Timber.e(apiException)
        Either.Left(apiException)
    }
}

suspend fun Task<Void>.awaitNoResult(): Either<ENStatus, Void?> = await().let {
    val result = it.right
    val exception = it.left
    return if (exception != null) {
        Either.Left(ENStatus(exception))
    } else {
        Either.Right(result)
    }
}

suspend fun <T> Task<T>.awaitWithStatus(): Either<ENStatus, T> = await().let {
    val result = it.right
    val exception = it.left
    return if (result != null) {
        Either.Right(result)
    } else {
        Either.Left(ENStatus(exception))
    }
}