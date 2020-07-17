package org.covidwatch.android.ui

import android.widget.ImageView
import androidx.annotation.DrawableRes
import org.covidwatch.android.R
import org.covidwatch.android.data.DefaultRegions.ARIZONA_ID
import org.covidwatch.android.data.DefaultRegions.THE_ASU_ID
import org.covidwatch.android.data.DefaultRegions.THE_NAU_ID
import org.covidwatch.android.data.DefaultRegions.THE_UOA_ID
import org.covidwatch.android.data.Region

val Region.bigLogo
    @DrawableRes
    get() = when (id) {
        ARIZONA_ID -> R.drawable.cw_big_logo
        THE_UOA_ID -> R.drawable.uoa_big_logo
        THE_ASU_ID -> R.drawable.asu_big_logo
        THE_NAU_ID -> R.drawable.nau_big_logo
        else -> R.drawable.cw_big_logo
    }

val Region.logo
    @DrawableRes
    get() = when (id) {
        ARIZONA_ID -> R.drawable.cw_logo
        THE_UOA_ID -> R.drawable.uoa_logo
        THE_ASU_ID -> R.drawable.asu_logo
        THE_NAU_ID -> R.drawable.nau_logo
        else -> R.drawable.cw_logo
    }

fun ImageView.setBigLogo(region: Region) {
    setImageResource(region.bigLogo)
}

fun ImageView.setLogo(region: Region) {
    setImageResource(region.logo)
}