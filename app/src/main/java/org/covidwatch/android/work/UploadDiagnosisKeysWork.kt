package org.covidwatch.android.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.covidwatch.android.domain.UploadDiagnosisKeysUseCase
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.extension.FAILURE
import org.covidwatch.android.ui.Notifications
import org.covidwatch.android.ui.Notifications.Companion.UPLOADING_REPORT_NOTIFICATION_ID
import org.koin.java.KoinJavaComponent.inject

class UploadDiagnosisKeysWork(
    context: Context,
    private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    private val uploadDiagnosisKeysWorkUseCase: UploadDiagnosisKeysUseCase by inject(
        UploadDiagnosisKeysUseCase::class.java
    )
    private val notifications: Notifications by inject(Notifications::class.java)

    override suspend fun doWork() = withContext(Dispatchers.IO) {
        val riskLevels = workerParams.inputData.getIntArray(RISK_LEVELS)?.toList()
        setForeground(
            ForegroundInfo(
                UPLOADING_REPORT_NOTIFICATION_ID,
                notifications.uploadingReportNotification()
            )
        )
        val params = riskLevels?.let {
            UploadDiagnosisKeysUseCase.Params(it)
        }
        uploadDiagnosisKeysWorkUseCase.run(params).apply {
            success { return@withContext Result.success() }
            failure { return@withContext failure(it) }
        }
        return@withContext Result.success()
    }

    private fun failure(status: ENStatus) = Result.failure(
        Data.Builder().putInt(FAILURE, status.code).build()
    )

    companion object {
        const val RISK_LEVELS = "RISK_LEVELS"
    }
}