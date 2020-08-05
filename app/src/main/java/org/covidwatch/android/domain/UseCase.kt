package org.covidwatch.android.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.covidwatch.android.exposurenotification.Failure
import org.covidwatch.android.functional.Either

/**
 * Abstract class for a Use Case (Interactor in terms of Clean Architecture).
 * This abstraction represents an execution unit for different use cases (this means than any use
 * case in the application should implement this contract).
 *
 * By convention each [UseCase] implementation will execute its job in a background thread
 * (kotlin coroutine) and will post the result in the UI thread.
 */
abstract class UseCase<Type, in Params>(
    protected val dispatchers: AppCoroutineDispatchers
) {

    abstract suspend fun run(params: Params? = null): Either<Failure, Type>

    operator fun invoke(
        scope: CoroutineScope,
        params: Params? = null,
        onResult: suspend (Either<Failure, Type>) -> Unit = {}
    ) {
        val job = scope.async(dispatchers.io) { run(params) }
        scope.launch(dispatchers.main) { onResult(job.await()) }
    }

    suspend operator fun invoke(
        params: Params? = null
    ) = run(params)
}