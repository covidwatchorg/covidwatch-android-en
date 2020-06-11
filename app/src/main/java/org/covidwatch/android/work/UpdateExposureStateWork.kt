package org.covidwatch.android.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationStatusCodes.FAILED
import org.covidwatch.android.data.EnConverter
import org.covidwatch.android.data.diagnosiskeystoken.DiagnosisKeysTokenRepository
import org.covidwatch.android.data.exposureinformation.ExposureInformationRepository
import org.covidwatch.android.data.pref.PreferenceStorage
import org.covidwatch.android.domain.UpdateExposureInformationUseCase
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber

class UpdateExposureStateWork(
    context: Context,
    private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val exposureNotification by inject(ExposureNotificationManager::class.java)
    private val diagnosisKeysTokenRepository by inject(DiagnosisKeysTokenRepository::class.java)
    private val exposureInformationRepository by inject(ExposureInformationRepository::class.java)

    private val preferenceStorage by inject(PreferenceStorage::class.java)
    private val enConverter by inject(EnConverter::class.java)
    private val updateExposureInformationUseCase by inject(UpdateExposureInformationUseCase::class.java)

    override suspend fun doWork(): Result {
        val token =
            workerParams.inputData.getString(PARAM_TOKEN) ?: return failure(FAILED)
        Timber.d("Start UpdateExposureStateWork for token: $token")

        val exposureSummaryResult = exposureNotification.getExposureSummary(token)
        val exposureSummary =
            exposureSummaryResult.right ?: return failure(exposureSummaryResult.left?.statusCode)

        if (exposureSummary.matchedKeyCount > 0) {
            diagnosisKeysTokenRepository.setExposed(token)

            updateExposureInformationUseCase.run()

            val exposures = exposureInformationRepository.exposures()
            val maxRiskScore = exposures.maxBy { it.totalRiskScore }?.totalRiskScore
            val summationRiskScore = exposures.sumBy { it.totalRiskScore }

            val covidExposureSummary = enConverter.covidExposureSummary(exposureSummary)

            preferenceStorage.exposureSummary = covidExposureSummary.copy(
                maximumRiskScore = maxRiskScore ?: covidExposureSummary.maximumRiskScore,
                summationRiskScore = summationRiskScore
            )
        } else {
            diagnosisKeysTokenRepository.delete(token)
        }
        return Result.success()
    }

    private fun failure(status: Int?) =
        Result.failure(Data.Builder().putInt(FAILURE, status ?: UNKNOWN_FAILURE).build())

    companion object {
        const val FAILURE = "status"
        const val UNKNOWN_FAILURE = -1
        const val PARAM_TOKEN = "token"
    }
}