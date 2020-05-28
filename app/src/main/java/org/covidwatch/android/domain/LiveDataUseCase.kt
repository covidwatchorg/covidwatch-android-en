package org.covidwatch.android.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import kotlinx.coroutines.CoroutineScope
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.functional.Either

abstract class LiveDataUseCase<Type, in Params>(
    dispatchers: AppCoroutineDispatchers
) : UseCase<Type, Params>(dispatchers) {

    abstract suspend fun observe(params: Params? = null): LiveData<Either<ENStatus, Type>>

    operator fun invoke(scope: CoroutineScope, params: Params? = null) =
        liveData(scope.coroutineContext + dispatchers.io) {
            emitSource(observe(params))
        }
}