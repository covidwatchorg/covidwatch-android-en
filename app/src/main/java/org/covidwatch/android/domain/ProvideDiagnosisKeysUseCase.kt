package org.covidwatch.android.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.work.*
import org.covidwatch.android.BuildConfig
import org.covidwatch.android.domain.ProvideDiagnosisKeysUseCase.Params
import org.covidwatch.android.exposurenotification.Failure
import org.covidwatch.android.extension.getFinalWorkInfoByIdLiveData
import org.covidwatch.android.functional.Either
import org.covidwatch.android.work.ProvideDiagnosisKeysWork
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

class ProvideDiagnosisKeysUseCase(
    private val workManager: WorkManager,
    dispatchers: AppCoroutineDispatchers
) : LiveDataUseCase<UUID, Params>(dispatchers) {
    override suspend fun run(params: Params?): Either<Failure, UUID> {
        val recurrent = params?.recurrent ?: false
        Timber.d("Start ${javaClass.simpleName}. Recurrent: $recurrent")

        val downloadRequest: WorkRequest
        if (recurrent) {
            downloadRequest = PeriodicWorkRequestBuilder<ProvideDiagnosisKeysWork>(
                BuildConfig.EXPOSURE_CHECKS_PERIOD,
                TimeUnit.HOURS
            ).apply {
                setConstraints(
                    Constraints
                        .Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .setRequiresBatteryNotLow(true)
                        .setRequiresStorageNotLow(true)
                        .build()
                )
                setInitialDelay(BuildConfig.EXPOSURE_CHECKS_PERIOD, TimeUnit.HOURS)
                setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    TimeUnit.HOURS.toMillis(3), // 3 hours and 6 hours between retries
                    TimeUnit.MILLISECONDS
                )
            }.build()

            workManager.enqueueUniquePeriodicWork(
                RECURRENT_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                downloadRequest
            )
        } else {
            downloadRequest = OneTimeWorkRequestBuilder<ProvideDiagnosisKeysWork>().build()

            workManager.enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                downloadRequest
            )
        }
        return Either.Right(downloadRequest.id)
    }

    override suspend fun observe(params: Params?): LiveData<Either<Failure, UUID>> = liveData {
        run(params).apply {
            success { emitSource(workManager.getFinalWorkInfoByIdLiveData(it)) }
            failure { emit(Either.Left(it)) }
        }
    }

    data class Params(val recurrent: Boolean)

    companion object {
        const val WORK_NAME = "provide_diagnosis_keys"
        const val RECURRENT_WORK_NAME = "provide_diagnosis_keys_daily"
    }
}