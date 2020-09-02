package org.covidwatch.android.di

import android.content.Context
import android.content.ContextWrapper

// exists solely to make the Android applicationContext injectable
class AppContext(context : Context) : ContextWrapper(context) {
}