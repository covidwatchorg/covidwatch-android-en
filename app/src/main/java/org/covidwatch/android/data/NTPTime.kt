package org.covidwatch.android.data

import com.lyft.kronos.AndroidClockFactory
import com.lyft.kronos.KronosClock
import org.covidwatch.android.BaseCovidWatchApplication

object NTPTime {
    private val kronosClock : KronosClock = AndroidClockFactory.createKronosClock(
        BaseCovidWatchApplication.appContext)

    init {
        kronosClock.syncInBackground()
    }

    fun currentTimeMillis() : Long = kronosClock.getCurrentTimeMs()
}
