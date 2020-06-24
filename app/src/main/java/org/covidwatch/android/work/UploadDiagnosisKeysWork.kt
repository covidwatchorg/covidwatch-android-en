package org.covidwatch.android.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
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
    private val positiveDiagnosisRepository by inject(PositiveDiagnosisRepository::class.java)

    private val notifications: Notifications by inject(Notifications::class.java)

    override suspend fun doWork() = withContext(Dispatchers.IO) {
        val riskLevels = workerParams.inputData.getIntArray(RISK_LEVELS)?.toList()
            ?: return@withContext Result.failure()
        val positiveReportId = workerParams.inputData.getString(DIAGNOSIS_REPORT)
            ?: return@withContext Result.failure()

        val report = positiveDiagnosisRepository.positiveDiagnosisReport(positiveReportId)

        Timber.d("Start ${javaClass.simpleName}. Risk Levels: ${riskLevels.joinToString()}")

        setForeground(
            ForegroundInfo(
                UPLOADING_REPORT_NOTIFICATION_ID,
                notifications.uploadingReportNotification()
            )
        )

        val params = UploadDiagnosisKeysUseCase.Params(riskLevels, report)
        uploadDiagnosisKeysWorkUseCase.run(params).apply {
            success { return@withContext Result.success() }
            failure { return@withContext failure(it) }
        }
        return@withContext Result.success()
    }

    companion object {
        const val RISK_LEVELS = "RISK_LEVELS"
        const val DIAGNOSIS_REPORT = "DIAGNOSIS_REPORT"
    }
}