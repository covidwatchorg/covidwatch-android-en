package org.covidwatch.android

import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class CrashlyticsTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        FirebaseCrashlytics.getInstance().log(message)
        t?.let { FirebaseCrashlytics.getInstance().recordException(it) }
    }
}