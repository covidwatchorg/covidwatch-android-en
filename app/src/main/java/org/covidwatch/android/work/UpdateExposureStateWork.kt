package org.covidwatch.android.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import org.covidwatch.android.R
import org.covidwatch.android.data.CovidExposureSummary
import org.covidwatch.android.data.diagnosiskeystoken.DiagnosisKeysTokenRepository
import org.covidwatch.android.data.pref.PreferenceStorage
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.exposurenotification.Status
import org.covidwatch.android.ui.exposurenotification.ExposureNotificationActivity
import org.koin.java.KoinJavaComponent.inject

class UpdateExposureStateWork(
    private val context: Context,
    private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val exposureNotification by inject(ExposureNotificationManager::class.java)
    private val diagnosisKeysTokenRepository by inject(DiagnosisKeysTokenRepository::class.java)
    private val preferenceStorage by inject(PreferenceStorage::class.java)

    override suspend fun doWork(): Result {
        val token =
            workerParams.inputData.getString(PARAM_TOKEN) ?: return failure(Status.FAILED_INTERNAL)

        val exposureSummaryResult = exposureNotification.getExposureSummary(token)
        val exposureSummary =
            exposureSummaryResult.right ?: return failure(exposureSummaryResult.left)

        // TODO: Check if order of updates is preserved relatively to the calls of [ProvideDiagnosisKeysUseCase]
        // If not, older updates could override new exposure summary
        preferenceStorage.exposureSummary = CovidExposureSummary(
            exposureSummary.daysSinceLastExposure,
            exposureSummary.matchedKeyCount,
            exposureSummary.maximumRiskScore,
            exposureSummary.attenuationDurationsInMinutes,
            exposureSummary.summationRiskScore
        )

        if (exposureSummary.matchedKeyCount > 0) {
            diagnosisKeysTokenRepository.setExposed(token)
            postNotification()
        } else {
            diagnosisKeysTokenRepository.delete(token)
        }
        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                EXPOSURE_NOTIFICATION_CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = context.getString(R.string.notification_channel_description)
            val notificationManager = context.getSystemService(
                NotificationManager::class.java
            )
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun postNotification() {
        createNotificationChannel()
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, ExposureNotificationActivity::class.java),
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

        val notificationManager = NotificationManagerCompat
            .from(context)
        notificationManager.notify(0, builder.build())
    }

    private fun failure(status: Int?) =
        Result.failure(Data.Builder().putInt(FAILURE, status ?: UNKNOWN_FAILURE).build())

    companion object {
        const val EXPOSURE_NOTIFICATION_CHANNEL_ID = "EXPOSURE_NOTIFICATION_CHANNEL_ID"
        const val FAILURE = "status"
        const val UNKNOWN_FAILURE = -1
        const val PARAM_TOKEN = "token"
    }
}