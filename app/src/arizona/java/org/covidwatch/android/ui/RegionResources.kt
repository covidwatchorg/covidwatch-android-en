package org.covidwatch.android.ui

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import org.covidwatch.android.R
import org.covidwatch.android.data.RegionId.ARIZONA_STATE
import org.covidwatch.android.data.RegionId.ASU
import org.covidwatch.android.data.RegionId.NAU
import org.covidwatch.android.data.RegionId.UOA
import org.covidwatch.android.data.model.Region

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

val Region.logoDescription
    @StringRes
    get() = when (id) {
        ARIZONA_STATE -> R.string.az_logo_content_description
        UOA -> R.string.uoa_logo_content_description
        ASU -> R.string.asu_logo_content_description
        NAU -> R.string.nau_logo_content_description
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