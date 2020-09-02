package org.covidwatch.android.data

import android.content.Context
import com.lyft.kronos.AndroidClockFactory
import com.lyft.kronos.KronosClock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class NtpTime (context : Context) {
    private val kronosClock : KronosClock = AndroidClockFactory.createKronosClock(context)

    private fun currentTimeMillis() : Long = kronosClock.getCurrentTimeMs()

    fun syncInBackground() = kronosClock.syncInBackground()

    fun nowAsLocalDate() : LocalDate =
        Instant.ofEpochMilli(currentTimeMillis()).atZone(ZoneId.systemDefault()).toLocalDate()
}
