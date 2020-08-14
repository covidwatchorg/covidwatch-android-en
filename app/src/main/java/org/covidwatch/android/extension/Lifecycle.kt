package org.covidwatch.android.extension

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import org.covidwatch.android.ui.event.Event
import org.covidwatch.android.ui.event.EventObserver
import org.covidwatch.android.ui.event.NullableEvent
import org.covidwatch.android.ui.event.NullableEventObserver

fun <T : Any?, L : LiveData<T>> LifecycleOwner.observe(liveData: L, body: (T) -> Unit = {}) =
    liveData.observe(this, Observer(body))

fun <T : Any?, L : LiveData<Event<T>>> LifecycleOwner.observeEvent(liveData: L, body: (T) -> Unit) =
    liveData.observe(this, EventObserver(body))

fun <T : Any?, L : LiveData<NullableEvent<T?>>> LifecycleOwner.observeNullableEvent(
    liveData: L,
    body: (T?) -> Unit
) = liveData.observe(this, NullableEventObserver(body))