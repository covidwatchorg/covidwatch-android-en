package org.covidwatch.android

import timber.log.Timber

class CovidWatchApplication : BaseCovidWatchApplication() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(CrashlyticsTree())
    }
}
