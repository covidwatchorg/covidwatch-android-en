package org.covidwatch.android

import timber.log.Timber

class CovidWatchApplication : BaseCovidWatchApplication() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(CrashlyticsTree())
    }
}
