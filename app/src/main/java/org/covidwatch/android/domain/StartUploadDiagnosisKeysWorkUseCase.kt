package org.covidwatch.android.domain

import androidx.lifecycle.LiveData
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.extension.getFinalWorkInfoByIdLiveData
import org.covidwatch.android.functional.Either
import org.covidwatch.android.work.UploadDiagnosisKeysWork


class StartUploadDiagnosisKeysWorkUseCase(
    private val workManager: WorkManager
) : LiveDataUseCase<Unit, Unit>() {

    override fun observe(params: Unit?): LiveData<Either<ENStatus, Unit>> {
        val downloadRequest = OneTimeWorkRequestBuilder<UploadDiagnosisKeysWork>()
            .setConstraints(
                Constraints.Builder()
                    .build()
            )
            .build()

        workManager.enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            downloadRequest
        )
        return workManager.getFinalWorkInfoByIdLiveData(downloadRequest.id)
    }

    companion object {
        const val WORK_NAME = "upload_diagnosis_keys"
    }
}