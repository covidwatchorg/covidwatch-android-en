package org.covidwatch.android.ui

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.covidwatch.android.R
import org.covidwatch.android.ui.BaseMainActivity.Companion.POTENTIAL_EXPOSURE_NOTIFICATION

class Notifications(private val context: Context) {
    private val notificationManager = NotificationManagerCompat.from(context)

    fun postExposureNotification() {
        createExposureNotificationChannel()
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(POTENTIAL_EXPOSURE_NOTIFICATION, true)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(
            context,
            EXPOSURE_NOTIFICATION_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_message))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(context.getString(R.string.notification_message))
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(EXPOSURE_NOTIFICATION_ID, builder.build())
    }

    private fun createExposureNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                EXPOSURE_NOTIFICATION_CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = context.getString(R.string.notification_channel_description)

            notificationManager.createNotificationChannel(channel)
        }
    }


    fun uploadingReportNotification(): Notification {
        createUploadReportChannel()
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder = NotificationCompat.Builder(
            context,
            UPLOAD_REPORT_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.upload_report_notification_title))
            .setStyle(
                NotificationCompat.BigTextStyle()
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        return builder.build()
    }

    private fun createUploadReportChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                UPLOAD_REPORT_CHANNEL_ID,
                context.getString(R.string.upload_report_notification_channel),
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description =
                context.getString(R.string.upload_report_notification_channel_description)

            notificationManager.createNotificationChannel(channel)
        }
    }

    fun downloadingReportsNotification(): Notification {
        createDownloadReportChannel()
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder = NotificationCompat.Builder(
            context,
            DOWNLOAD_REPORTS_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.download_reports_notification_title))
            .setStyle(
                NotificationCompat.BigTextStyle()
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        return builder.build()
    }

    private fun createDownloadReportChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                DOWNLOAD_REPORTS_CHANNEL_ID,
                context.getString(R.string.download_reports_notification_channel),
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description =
                context.getString(R.string.download_reports_notification_channel_description)

            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val EXPOSURE_NOTIFICATION_CHANNEL_ID = "EXPOSURE_NOTIFICATION_CHANNEL_ID"
        const val UPLOAD_REPORT_CHANNEL_ID = "UPLOAD_REPORT_CHANNEL_ID"
        const val DOWNLOAD_REPORTS_CHANNEL_ID = "DOWNLOAD_REPORTS_CHANNEL_ID"
        const val EXPOSURE_NOTIFICATION_ID = 55
        const val UPLOADING_REPORT_NOTIFICATION_ID = 66
        const val DOWNLOAD_REPORTS_NOTIFICATION_ID = 77
    }
}