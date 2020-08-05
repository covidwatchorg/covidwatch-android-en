package org.covidwatch.android.ui

import android.app.Activity
import android.util.SparseArray
import androidx.core.util.contains
import androidx.lifecycle.*
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.covidwatch.android.domain.LiveDataUseCase
import org.covidwatch.android.exposurenotification.Failure
import org.covidwatch.android.extension.send
import org.covidwatch.android.functional.Either
import org.covidwatch.android.ui.event.Event

/**
 * Base ViewModel class with default Failure handling.
 * @see ViewModel
 * @see Failure
 */
abstract class BaseViewModel : ViewModel() {
    private val _status = MediatorLiveData<Event<Failure>>()
    val status: LiveData<Event<Failure>> = _status

    private val _resolvable = MutableLiveData<Event<Resolvable>>()
    val resolvable: LiveData<Event<Resolvable>> = _resolvable

    private val tasksInResolution = SparseArray<(suspend () -> Any)?>()

    protected fun handleStatus(status: Failure) {
        _status.send(status)
    }

    protected suspend fun <V> withPermission(
        requestCode: Int,
        task: suspend () -> Either<Failure, V>
    ) {
        if (!tasksInResolution.contains(requestCode)) {
            task().apply {
                failure { handleFailure(it, requestCode, task) }
            }
        }
    }

    private fun handleFailure(status: Failure, requestCode: Int?, task: suspend () -> Any) =
        if (status is Failure.EnStatus.NeedsResolution && requestCode != null) {
            status.exception?.let {
                _resolvable.send(Resolvable(it, requestCode))
                tasksInResolution.put(requestCode, task)
            }
        } else {
            handleStatus(status)
        }

    protected fun <R : Failure, L> Either<R, L>.result(): L? {
        left?.let { handleStatus(it) }
        return right
    }

    protected fun <T, P> observeStatus(
        useCase: LiveDataUseCase<T, P>,
        params: P? = null,
        block: suspend (Either<Failure, T>) -> Unit = {}
    ) {
        viewModelScope.launch {
            useCase(this, params).asFlow().collect {
                it.failure(this@BaseViewModel::handleStatus)
                block(it)
            }
        }
    }

    suspend fun handleResolution(requestCode: Int, resultCode: Int) {
        val task = tasksInResolution.get(requestCode)
        if (resultCode == Activity.RESULT_OK) {
            task?.invoke()
        } else {
            handleStatus(Failure.EnStatus.RejectedOptIn)
        }
        tasksInResolution.remove(requestCode)
    }

    data class Resolvable(
        val apiException: ApiException,
        val requestCode: Int
    )
}