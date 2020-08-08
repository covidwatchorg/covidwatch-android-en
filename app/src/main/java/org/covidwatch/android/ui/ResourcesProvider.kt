package org.covidwatch.android.ui

import android.content.Context
import androidx.annotation.StringRes

interface IResourcesProvider {
    fun getString(@StringRes resId: Int): String
}

class ResourcesProvider(private val context: Context) : IResourcesProvider {
    override fun getString(resId: Int) = context.getString(resId)
}