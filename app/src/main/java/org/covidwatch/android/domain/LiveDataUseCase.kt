package org.covidwatch.android.domain

import androidx.lifecycle.LiveData
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.functional.Either

abstract class LiveDataUseCase<Type, in Params> {

    abstract fun observe(params: Params? = null): LiveData<Either<ENStatus, Type>>
}