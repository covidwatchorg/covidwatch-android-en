package org.covidwatch.android.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.nearby.exposurenotification.DiagnosisKeysDataMapping
import com.google.android.gms.nearby.exposurenotification.Infectiousness
import com.google.android.gms.nearby.exposurenotification.ReportType
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber

class SetDiagnosisKeysDataMappingWork(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    private val enManager by inject(ExposureNotificationManager::class.java)

    override suspend fun doWork(): Result {
        Timber.d("Start ${javaClass.simpleName}.")

        // TODO: 09/09/2020 Fetch configuration from regions JSON data or somewhere else
        enManager.diagnosisKeysDataMapping(diagnosisKeysDataMapping).apply {
            success { Timber.d("Set diagnosis keys data mapping to $diagnosisKeysDataMapping") }
            failure { Timber.d("Failed to set diagnosis keys data mapping: $it") }
        }
        return Result.success()
    }

    /**
     * Configures the interpretation of diagnosis key data to create ExposureWindow data.
     */
    private val diagnosisKeysDataMapping: DiagnosisKeysDataMapping by lazy {
        val daysToInfectiousness = mutableMapOf<Int, Int>()
        for (i in -14..14) {
            when (i) {
                in -5..-3 -> daysToInfectiousness[i] = Infectiousness.STANDARD
                in -2..5 -> daysToInfectiousness[i] = Infectiousness.HIGH
                in 6..10 -> daysToInfectiousness[i] = Infectiousness.STANDARD
                else -> daysToInfectiousness[i] = Infectiousness.NONE
            }
        }
        DiagnosisKeysDataMapping.DiagnosisKeysDataMappingBuilder()
            .setDaysSinceOnsetToInfectiousness(daysToInfectiousness)
            .setInfectiousnessWhenDaysSinceOnsetMissing(Infectiousness.STANDARD)
            .setReportTypeWhenMissing(ReportType.CONFIRMED_TEST)
            .build()
    }
}