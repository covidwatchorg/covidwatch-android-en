package org.covidwatch.android

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.analytics.FirebaseAnalytics
import android.os.Bundle

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

fun setAnalyticsInstance(instance: FirebaseAnalytics){
    firebaseAnalytics = instance
}

fun sendEvent(name: String){
    if (isTesting == false) return
    val bundle = Bundle()
    /*
    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, getFirebaseId())
    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name)
    firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
    */
    bundle.putString("item_id", getFirebaseId())
    bundle.putString("item_name", name)
    firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
    firebaseAnalytics?.logEvent("covid_watch_item", bundle)

}

