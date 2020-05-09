package org.covidwatch.android

import android.content.Context

/*
This is not a class because we need a "static" variable to hold the isTesting value
 */

var isTesting: Boolean = false

fun getFirebaseId(): String {
    return ""
}

fun setTester(testing: Boolean) {
    isTesting = testing
}

fun setAnalyticsInstanceFromContext(instance: Context){
    return
}

/*
To use Firebase DebugView, type this into the console:
adb shell setprop debug.firebase.analytics.app "edu.stanford.covidwatch.android"
 */

fun sendEvent(name: String){
    return
}

