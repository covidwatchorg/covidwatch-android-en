package org.covidwatch.android.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings

object Intents {
    val Context.playStoreWithServices: Intent?
        get() {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(
                    "https://play.google.com/store/apps/details?id=com.google.android.gms"
                )
                setPackage("com.android.vending")
            }

            val playStoreAvailable = packageManager.queryIntentActivities(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            ).isNotEmpty()

            return if (playStoreAvailable) intent else null
        }

    fun browser(url: String) = Intent(Intent.ACTION_VIEW, Uri.parse(url))

    val wirelessSettings: Intent
        get() = Intent(Settings.ACTION_WIRELESS_SETTINGS)

    fun Context.openBrowser(url: String?) = url?.let {
        startActivity(browser(it))
    }

    fun Context.dial(url: String?) = url?.let {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse(it)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
}