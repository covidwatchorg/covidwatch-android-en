package org.covidwatch.android.ui

import android.app.Activity
import android.util.SparseArray
import androidx.core.util.contains
import androidx.lifecycle.*
import com.google.android.gms.common.api.ApiException
import org.covidwatch.android.domain.LiveDataUseCase
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.extension.observeUseCase
import org.covidwatch.android.extension.send
import org.covidwatch.android.functional.Either
import org.covidwatch.android.ui.event.Event

/**
 * Base ViewModel class with default ENStatus handling.
 * @see ViewModel
 * @see ENStatus
 */
abstract class BaseViewModel : ViewModel() {
    private val _status = MediatorLiveData<Event<ENStatus>>()
    val status: MutableLiveData<Event<ENStatus>> = _status

    private val _resolvable = MutableLiveData<Event<Resolvable>>()
    val resolvable: LiveData<Event<Resolvable>> = _resolvable

    private val tasksInResolution = SparseArray<suspend () -> Any>()

    protected fun handleStatus(status: ENStatus) {
        _status.send(status)
    }

    protected suspend fun <V> withPermission(
        requestCode: Int,
        task: suspend () -> Either<ENStatus, V>
    ) {
        if (!tasksInResolution.contains(requestCode)) {
            task().apply {
                failure {
                    if (it is ENStatus.NeedsResolution) {
                        _resolvable.send(Resolvable(it.exception, requestCode))
                        tasksInResolution.put(requestCode, task)
                    } else {
                        handleStatus(it)
                    }
                }
            }
        }
    }

    protected fun <T, P> observeStatus(
        useCase: LiveDataUseCase<T, P>,
        params: P? = null,
        block: (Either<ENStatus, T>) -> Unit = {}
    ) {
        val liveData = viewModelScope.observeUseCase(useCase, params)
        _status.addSource(liveData) {
            it.failure(this::handleStatus)
            block(it)
            _status.removeSource(liveData)
        }
    }

    suspend fun handleResolution(requestCode: Int, resultCode: Int) {
        val task = tasksInResolution.get(requestCode)
        tasksInResolution.remove(requestCode)
        if (resultCode == Activity.RESULT_OK) {
            val result = task() as Either<*, *>
            val status = result.left as? ENStatus
            status?.let { handleStatus(it) }
        } else {
            handleStatus(ENStatus.FailedRejectedOptIn)
        }
    }

    data class Resolvable(
        val apiException: ApiException,
        val requestCode: Int
    )
}