package org.covidwatch.android.extension

import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat

fun View.setRippleBackground() = with(TypedValue()) {
    context.theme.resolveAttribute(
        android.R.attr.selectableItemBackgroundBorderless,
        this,
        true
    )
    foreground = ContextCompat.getDrawable(context, resourceId)
}