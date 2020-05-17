package org.covidwatch.android.extension

import androidx.lifecycle.*
import org.covidwatch.android.ui.event.Event

fun <X> LiveData<X>.doOnNext(body: (X) -> Unit): LiveData<X> {
    val result = MediatorLiveData<X>()
    result.addSource(this) { x ->
        body(x)
        result.value = x
    }
    return result
}

fun <T : Any> mutableLiveData(defaultValue: T) =
    MutableLiveData<T>().apply { value = defaultValue }

fun <T : Any> MutableLiveData<Event<T>>.set(value: T) {
    this.value = Event(value)
}

/**
 * Send an empty event
 * */
fun MutableLiveData<Event<Unit>>.send() {
    this.value = Event(Unit)
}