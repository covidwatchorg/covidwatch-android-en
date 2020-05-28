package org.covidwatch.android.domain

import androidx.lifecycle.liveData
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.extension.getFinalWorkInfoByIdLiveData
import org.covidwatch.android.functional.Either
import org.covidwatch.android.work.UploadDiagnosisKeysWork
import java.util.*


class StartUploadDiagnosisKeysWorkUseCase(
    private val workManager: WorkManager,
    dispatchers: AppCoroutineDispatchers
) : LiveDataUseCase<UUID, Unit>(dispatchers) {

    override suspend fun run(params: Unit?): Either<ENStatus, UUID> {
        val uploadRequest = OneTimeWorkRequestBuilder<UploadDiagnosisKeysWork>()
            .setConstraints(Constraints.Builder().build())
            .build()

        workManager.enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            uploadRequest
        )

        return Either.Right(uploadRequest.id)
    }

    override suspend fun observe(params: Unit?) = liveData {
        run(params).apply {
            success { emitSource(workManager.getFinalWorkInfoByIdLiveData(it)) }
            failure { emit(Either.Left(it)) }
        }
    }

    companion object {
        const val WORK_NAME = "upload_diagnosis_keys"
    }
}