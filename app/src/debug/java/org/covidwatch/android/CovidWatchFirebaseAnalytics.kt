package org.covidwatch.android

import android.content.Context
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.analytics.FirebaseAnalytics
import android.os.Bundle
import java.util.Date

/*
This is not a class because we need a "static" variable to hold the isTesting value
 */

var isTesting: Boolean = false
var firebaseAnalytics: FirebaseAnalytics? = null

fun getFirebaseId(): String {
    val instanceId = FirebaseInstanceId.getInstance().id
    return instanceId
}

fun setTester(testing: Boolean) {
    isTesting = testing
}

fun setAnalyticsInstanceFromContext(context: Context){
    firebaseAnalytics = FirebaseAnalytics.getInstance(context)
}

/*
To use Firebase DebugView, type this into the console:
adb shell setprop debug.firebase.analytics.app "edu.stanford.covidwatch.android"
 */

fun sendEvent(name: String){
    if (isTesting == false) return
    val bundle = Bundle()
    var date: Date = Date()

    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, getFirebaseId())
    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name)
    bundle.putString("event_date", date.toString())
    firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
}

