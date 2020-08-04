package org.covidwatch.android.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.covidwatch.android.domain.UploadDiagnosisKeysUseCase
import org.covidwatch.android.extension.failure
import org.covidwatch.android.ui.Notifications
import org.covidwatch.android.ui.Notifications.Companion.UPLOADING_REPORT_NOTIFICATION_ID
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber

class UploadDiagnosisKeysWork(
    context: Context,
    private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    private val uploadDiagnosisKeysWorkUseCase by inject(UploadDiagnosisKeysUseCase::class.java)
    private val notifications: Notifications by inject(Notifications::class.java)

    override suspend fun doWork() = withContext(Dispatchers.IO) {
        Timber.d("Start ${javaClass.simpleName}")

        val params = UploadDiagnosisKeysUseCase.Params.fromJson(
            workerParams.inputData.getString(PARAMS)
        ) ?: return@withContext Result.failure()

        setForeground(
            ForegroundInfo(
                UPLOADING_REPORT_NOTIFICATION_ID,
                notifications.uploadingReportNotification()
            )
        )

        uploadDiagnosisKeysWorkUseCase.run(params).apply {
            success { return@withContext Result.success() }
            failure { return@withContext failure(it) }
        }
        return@withContext Result.success()
    }

    companion object {
        const val PARAMS = "params"
    }
}