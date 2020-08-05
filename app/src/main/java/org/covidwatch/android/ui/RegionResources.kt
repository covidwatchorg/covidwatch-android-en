package org.covidwatch.android.ui

import android.widget.ImageView
import androidx.annotation.DrawableRes
import org.covidwatch.android.R
import org.covidwatch.android.data.Region
import org.covidwatch.android.data.RegionId.*

val Region.bigLogo
    @DrawableRes
    get() = when (id) {
        ARIZONA_STATE -> R.drawable.az_big_logo
        UOA -> R.drawable.uoa_big_logo
        ASU -> R.drawable.asu_big_logo
        NAU -> R.drawable.nau_big_logo
        else -> R.drawable.cw_big_logo
    }

val Region.logo
    @DrawableRes
    get() = when (id) {
        ARIZONA_STATE -> R.drawable.az_logo
        UOA -> R.drawable.uoa_logo
        ASU -> R.drawable.asu_logo
        NAU -> R.drawable.nau_logo
        else -> R.drawable.cw_logo
    }

fun ImageView.setBigLogo(region: Region) {
    setImageResource(region.bigLogo)
}

fun ImageView.setLogo(region: Region) {
    setImageResource(region.logo)
}