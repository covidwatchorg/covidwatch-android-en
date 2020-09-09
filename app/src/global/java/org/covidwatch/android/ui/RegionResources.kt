package org.covidwatch.android.ui

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import org.covidwatch.android.R
import org.covidwatch.android.data.Region
import org.covidwatch.android.data.RegionId.BERMUDA

val Region.bigLogo
    @DrawableRes
    get() = when (id) {
        BERMUDA -> R.drawable.bermuda_logo
        else -> R.drawable.cw_big_logo
    }

val Region.logo
    @DrawableRes
    get() = when (id) {
        BERMUDA -> R.drawable.bermuda_logo
        else -> R.drawable.cw_logo
    }

val Region.logoDescription
    @StringRes
    get() = when (id) {
        BERMUDA -> R.string.bermuda_logo_content_description
        else -> R.string.generic_logo_content_description
    }

fun ImageView.setBigLogo(region: Region) {
    setImageResource(region.bigLogo)
    contentDescription = context.getString(region.logoDescription)
}

fun ImageView.setLogo(region: Region) {
    setImageResource(region.logo)
    contentDescription = context.getString(region.logoDescription)
}