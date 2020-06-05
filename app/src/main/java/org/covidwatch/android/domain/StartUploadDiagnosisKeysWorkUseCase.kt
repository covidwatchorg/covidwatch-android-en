package org.covidwatch.android.domain

import androidx.lifecycle.liveData
import androidx.work.*
import org.covidwatch.android.domain.StartUploadDiagnosisKeysWorkUseCase.Params
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.extension.getFinalWorkInfoByIdLiveData
import org.covidwatch.android.functional.Either
import org.covidwatch.android.work.UploadDiagnosisKeysWork
import org.covidwatch.android.work.UploadDiagnosisKeysWork.Companion.RISK_LEVELS
import java.util.*


class StartUploadDiagnosisKeysWorkUseCase(
    private val workManager: WorkManager,
    dispatchers: AppCoroutineDispatchers
) : LiveDataUseCase<UUID, Params>(dispatchers) {

    override suspend fun run(params: Params?): Either<ENStatus, UUID> {

        val riskLevels = params?.riskLevels?.toIntArray()
        val data = riskLevels?.let { Data.Builder().putIntArray(RISK_LEVELS, it).build() }

        val uploadRequest = OneTimeWorkRequestBuilder<UploadDiagnosisKeysWork>()
            .apply {
                setConstraints(Constraints.Builder().build())
                data?.let { setInputData(it) }
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

    data class Params(val riskLevels: List<Int>)
}