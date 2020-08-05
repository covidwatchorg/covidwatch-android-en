package org.covidwatch.android.ui

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import org.covidwatch.android.R
import org.covidwatch.android.ui.BaseMainActivity.Companion.POTENTIAL_EXPOSURE_NOTIFICATION
import org.covidwatch.android.ui.Intents.playStoreWithServices

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

        val builder = NotificationCompat.Builder(context, EXPOSURE_NOTIFICATION_CHANNEL_ID)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
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
            .setStyle(NotificationCompat.BigTextStyle())
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        return builder.build()
    }

    private fun createUploadReportChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                UPLOAD_REPORT_CHANNEL_ID,
                context.getString(R.string.upload_report_notification_channel),
                NotificationManager.IMPORTANCE_HIGH
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
        val builder = NotificationCompat.Builder(context, DOWNLOAD_REPORTS_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.download_reports_notification_title))
            .setStyle(NotificationCompat.BigTextStyle())
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        return builder.build()
    }

    fun downloadingReportsNetworkFailure() {
        createDownloadReportChannel()

        val builder = NotificationCompat.Builder(context, DOWNLOAD_REPORTS_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.download_reports_failure_notification_title))
            .setContentText(context.getString(R.string.no_connection_error))
            .addAction(
                R.drawable.ic_settings_white_24dp,
                context.getString(R.string.open_settings),
                PendingIntent.getActivity(
                    context,
                    0,
                    Intents.wirelessSettings,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )

        notificationManager.notify(DOWNLOAD_REPORTS_ERROR_NOTIFICATION_ID, builder.build())
    }

    fun downloadingReportsEnNotAvailable() {
        createDownloadReportChannel()

        val builder = NotificationCompat.Builder(context, DOWNLOAD_REPORTS_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.download_reports_failure_notification_title))
            .setContentText(context.getString(R.string.notification_en_not_supported))
            .addAction(
                R.drawable.ic_external_link,
                context.getString(R.string.update),
                PendingIntent.getActivity(
                    context,
                    0,
                    context.playStoreWithServices,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )

        notificationManager.notify(DOWNLOAD_REPORTS_ERROR_NOTIFICATION_ID, builder.build())
    }

    fun downloadingReportsFailure(message: String, intent: Intent) {
        createDownloadReportChannel()

        val builder = NotificationCompat.Builder(context, DOWNLOAD_REPORTS_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.download_reports_failure_notification_title))
            .setContentText(message)
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .setAutoCancel(true)

        notificationManager.notify(DOWNLOAD_REPORTS_ERROR_NOTIFICATION_ID, builder.build())
    }

    fun downloadingReportsFailure(@StringRes message: Int, intent: Intent) {
        downloadingReportsFailure(context.getString(message), intent)
    }

    private fun createDownloadReportChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                DOWNLOAD_REPORTS_CHANNEL_ID,
                context.getString(R.string.download_reports_notification_channel),
                NotificationManager.IMPORTANCE_DEFAULT
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
        const val DOWNLOAD_REPORTS_ERROR_NOTIFICATION_ID = 78
    }
}