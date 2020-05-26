package org.covidwatch.android.extension

import android.content.Context
import android.content.Intent
import org.covidwatch.android.R

fun Context.shareApp() {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_content))
        type = "text/plain"
    }
    startActivity(Intent.createChooser(sendIntent, getString(R.string.share_app_title)))
}
