package org.covidwatch.android.domain

import androidx.work.*
import org.covidwatch.android.exposurenotification.ENStatus
import org.covidwatch.android.extension.toResult
import org.covidwatch.android.functional.Either
import org.covidwatch.android.work.ProvideDiagnosisKeysWork

class ProvideDiagnosisKeysUseCase(
    private val workManager: WorkManager,
    dispatchers: AppCoroutineDispatchers
) : UseCase<Unit, Unit>(dispatchers) {
    override suspend fun run(params: Unit?): Either<ENStatus, Unit> {
        val downloadRequest = OneTimeWorkRequestBuilder<ProvideDiagnosisKeysWork>()
            .setConstraints(
                Constraints.Builder()
//                    .setRequiresCharging(false)
//                    .setRequiresBatteryNotLow(true)
//                    .setRequiresDeviceIdle(true)
//                    .setRequiredNetworkType(NetworkType.UNMETERED)
                    .build()
            )
            .build()

        workManager.enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            downloadRequest
        )
        val workInfoLiveData = workManager.getWorkInfoById(downloadRequest.id)
        val workInfo = workInfoLiveData.await()

        return workInfo.toResult()
    }

    companion object {
        const val WORK_NAME = "provide_diagnosis_keys"
    }
}