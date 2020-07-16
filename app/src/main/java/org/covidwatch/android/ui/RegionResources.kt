package org.covidwatch.android.ui

import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
import org.covidwatch.android.R
import org.covidwatch.android.data.DefaultRegions.ARIZONA_ID
import org.covidwatch.android.data.DefaultRegions.THE_UOA_ID
import org.covidwatch.android.data.Region

@DrawableRes
fun Region.bigLogo(context: Context) = when (id) {
    ARIZONA_ID -> R.drawable.cw_big_logo
    THE_UOA_ID -> R.drawable.uoa_big_logo
    else -> R.drawable.cw_big_logo
}

@DrawableRes
fun Region.logo(context: Context) = when (id) {
    ARIZONA_ID -> R.drawable.cw_logo
    THE_UOA_ID -> R.drawable.uoa_logo
    else -> R.drawable.cw_logo
}

fun ImageView.setBigLogo(region: Region) {
    setImageResource(region.bigLogo(context))
}

fun ImageView.setLogo(region: Region) {
    setImageResource(region.logo(context))
}