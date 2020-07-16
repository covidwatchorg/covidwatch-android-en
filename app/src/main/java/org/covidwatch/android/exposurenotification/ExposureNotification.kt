package org.covidwatch.android.exposurenotification

import java.util.concurrent.TimeUnit

object ExposureNotification {

    val rollingInterval = TimeUnit.MINUTES.toMillis(10L)
}