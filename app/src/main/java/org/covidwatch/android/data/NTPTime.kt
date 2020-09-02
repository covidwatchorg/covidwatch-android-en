package org.covidwatch.android.data

import com.lyft.kronos.AndroidClockFactory
import com.lyft.kronos.KronosClock
import org.covidwatch.android.di.AppContext
import org.koin.core.KoinComponent
import org.koin.core.inject

class NTPTime {
    companion object : KoinComponent {
        val context : AppContext by inject()
        val kronosClock : KronosClock = AndroidClockFactory.createKronosClock(context)

        init {
            kronosClock.syncInBackground()
        }

        fun currentTimeMillis(): Long = kronosClock.getCurrentTimeMs()
    }
}
