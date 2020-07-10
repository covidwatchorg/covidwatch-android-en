package org.covidwatch.android.domain

import androidx.lifecycle.liveData
import androidx.work.*
import com.google.android.gms.nearby.exposurenotification.TemporaryExposureKey
import com.google.gson.Gson
import org.covidwatch.android.data.PositiveDiagnosisReport
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
import org.covidwatch.android.domain.StartUploadDiagnosisKeysWorkUseCase.Params
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.extension.getFinalWorkInfoByIdLiveData
import org.covidwatch.android.functional.Either
import org.covidwatch.android.work.UploadDiagnosisKeysWork
import org.covidwatch.android.work.UploadDiagnosisKeysWork.Companion.PARAMS
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

        val data = Data.Builder()
            .putString(
                PARAMS, Gson().toJson(
                    UploadDiagnosisKeysUseCase.Params(
                        params.keys,
                        params.positiveDiagnosisReport
                    )
                )
            )
            .build()

        Timber.d("Start ${javaClass.simpleName}.")

        val uploadRequest = OneTimeWorkRequestBuilder<UploadDiagnosisKeysWork>()
            .setInputData(data)
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
        val keys: List<TemporaryExposureKey>,
        val positiveDiagnosisReport: PositiveDiagnosisReport
    )
}