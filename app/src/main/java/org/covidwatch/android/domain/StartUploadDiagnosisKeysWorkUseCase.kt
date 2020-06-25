package org.covidwatch.android.domain

import androidx.lifecycle.liveData
import androidx.work.*
import org.covidwatch.android.data.PositiveDiagnosisReport
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
import org.covidwatch.android.domain.StartUploadDiagnosisKeysWorkUseCase.Params
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.extension.getFinalWorkInfoByIdLiveData
import org.covidwatch.android.functional.Either
import org.covidwatch.android.work.UploadDiagnosisKeysWork
import org.covidwatch.android.work.UploadDiagnosisKeysWork.Companion.DIAGNOSIS_REPORT
import org.covidwatch.android.work.UploadDiagnosisKeysWork.Companion.RISK_LEVELS
import timber.log.Timber
import java.util.*


class StartUploadDiagnosisKeysWorkUseCase(
    private val workManager: WorkManager,
    private val positiveDiagnosisRepository: PositiveDiagnosisRepository,
    dispatchers: AppCoroutineDispatchers
) : LiveDataUseCase<UUID, Params>(dispatchers) {

    override suspend fun run(params: Params?): Either<ENStatus, UUID> {
        params ?: return Either.Left(ENStatus.Failed)

        val positiveDiagnosisReport = params.positiveDiagnosisReport
        positiveDiagnosisRepository.addPositiveDiagnosisReport(positiveDiagnosisReport)

        val riskLevels = params.riskLevels.toIntArray()

        val data = Data.Builder()
            .putIntArray(RISK_LEVELS, riskLevels)
            .putString(DIAGNOSIS_REPORT, positiveDiagnosisReport.id)
            .build()

        Timber.d("Start ${javaClass.simpleName}. Risk Levels: ${riskLevels.joinToString()}")

        val uploadRequest = OneTimeWorkRequestBuilder<UploadDiagnosisKeysWork>()
            .apply {
                setConstraints(Constraints.Builder().build())
                setInputData(data)
            }
            .build()


        workManager.enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            uploadRequest
        )

        return Either.Right(uploadRequest.id)
    }

    override suspend fun observe(params: Params?) = liveData {
        run(params).apply {
            success { emitSource(workManager.getFinalWorkInfoByIdLiveData(it)) }
            failure { emit(Either.Left(it)) }
        }
    }

    companion object {
        const val WORK_NAME = "upload_diagnosis_keys"
    }

    data class Params(
        val riskLevels: List<Int>,
        val positiveDiagnosisReport: PositiveDiagnosisReport
    )
}