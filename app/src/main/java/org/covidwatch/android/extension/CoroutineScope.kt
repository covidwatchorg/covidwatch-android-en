package org.covidwatch.android.extension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.covidwatch.android.domain.LiveDataUseCase
import org.covidwatch.android.domain.UseCase
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.functional.Either

fun <T : Any?> CoroutineScope.io(block: suspend () -> T, result: (T) -> Unit) {
    launch {
        result(
            withContext(Dispatchers.IO) {
                block()
            }
        )
    }
}

fun <T : Any?> CoroutineScope.io(block: suspend () -> T) {
    launch {
        withContext(Dispatchers.IO) {
            block()
        }
    }
}

fun <Type : Any, Params> CoroutineScope.launchUseCase(
    useCase: UseCase<Type, Params>,
    params: Params? = null,
    onResult: suspend Either<ENStatus, Type>.() -> Unit = {}
) {
    useCase(this, params, onResult)
}

fun <Type, Params> CoroutineScope.observeUseCase(
    useCase: LiveDataUseCase<Type, Params>,
    params: Params? = null
) = useCase(this, params)