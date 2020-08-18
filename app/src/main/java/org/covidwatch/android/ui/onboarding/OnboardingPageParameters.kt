package org.covidwatch.android.ui.onboarding

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OnboardingPageParameters(
    @StringRes val title: Int,
    @DrawableRes val image: Int,
    @StringRes val imageDescription: Int,
    @StringRes val subtitle: Int
) : Parcelable