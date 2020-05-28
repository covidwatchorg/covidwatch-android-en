package org.covidwatch.android.ui

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.covidwatch.android.domain.LiveDataUseCase
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.extension.observeUseCase

/**
 * Base ViewModel class with default ENStatus handling.
 * @see ViewModel
 * @see ENStatus
 */
abstract class BaseViewModel : ViewModel() {
    private val _status = MediatorLiveData<ENStatus>()
    val status: MutableLiveData<ENStatus> = _status

    protected fun <T, P> observeStatus(useCase: LiveDataUseCase<T, P>, params: P? = null) {
        val liveData = viewModelScope.observeUseCase(useCase, params)
        _status.addSource(liveData) {
            it.failure { status ->
                _status.value = status
            }

            _status.removeSource(liveData)
        }
    }
}