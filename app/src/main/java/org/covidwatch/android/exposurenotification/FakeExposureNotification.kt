package org.covidwatch.android.exposurenotification

import com.google.android.gms.common.api.Api
import com.google.android.gms.common.api.internal.ApiKey
import com.google.android.gms.nearby.exposurenotification.*
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class FakeExposureNotification : ExposureNotificationClient {

    private var started = false

    override fun start(): Task<Void> {
        return Tasks.call {
            started = true
            null
        }
    }

    override fun stop(): Task<Void> {
        return Tasks.call {
            this.started = false
            null
        }
    }

    override fun getCalibrationConfidence(): Task<Int> {
        TODO("not implemented")
    }

    override fun isEnabled(): Task<Boolean> = Tasks.call { started }

    override fun getTemporaryExposureKeyHistory(): Task<List<TemporaryExposureKey>> =
        Tasks.call {
            List(1) { RandomEnObjects.temporaryExposureKey }
        }

    override fun provideDiagnosisKeys(
        keys: List<File>,
        configuration: ExposureConfiguration,
        token: String
    ): Task<Void> = Tasks.call { null }

    override fun provideDiagnosisKeys(p0: MutableList<File>?): Task<Void> {
        TODO("not implemented")
    }

    override fun getExposureSummary(token: String): Task<ExposureSummary> =
        Tasks.call { RandomEnObjects.exposureSummary }

    override fun getExposureInformation(token: String?): Task<List<ExposureInformation>> =
        Tasks.call {
            List(10) { RandomEnObjects.exposureInformation }
        }

    override fun getApiKey(): ApiKey<Api.ApiOptions.NoOptions> {
        TODO("not implemented")
    }

    override fun getVersion(): Task<Long> {
        TODO("not implemented")
    }

    override fun getDailySummaries(p0: DailySummariesConfig?): Task<MutableList<DailySummary>> {
        TODO("not implemented")
    }

    override fun deviceSupportsLocationlessScanning(): Boolean {
        TODO("not implemented")
    }

    override fun setDiagnosisKeysDataMapping(p0: DiagnosisKeysDataMapping?): Task<Void> {
        TODO("not implemented")
    }

    override fun getDiagnosisKeysDataMapping(): Task<DiagnosisKeysDataMapping> {
        TODO("not implemented")
    }

    override fun getExposureWindows(p0: String?): Task<MutableList<ExposureWindow>> {
        TODO("not implemented")
    }

    override fun getExposureWindows(): Task<MutableList<ExposureWindow>> {
        TODO("not implemented")
    }
}

object RandomEnObjects {
    val temporaryExposureKey: TemporaryExposureKey
        get() {
            val day = TimeUnit.DAYS.toMillis(1)
            val now = ((System.currentTimeMillis() - day) / 1000 / (60 * 10)).toInt()
            return TemporaryExposureKey.TemporaryExposureKeyBuilder()
                .setKeyData(Random.nextBytes(16))
                .setRollingStartIntervalNumber(now - 1)
                .setRollingPeriod(144)
                .setTransmissionRiskLevel(Random.nextInt(1, 8))
                .build()
        }

    val exposureSummary: ExposureSummary
        get() = ExposureSummary.ExposureSummaryBuilder()
            .setDaysSinceLastExposure(Random.nextInt(14))
            .setMatchedKeyCount(Random.nextInt(0, 4096))
            .setMaximumRiskScore(Random.nextInt(8))
            .build()

    val exposureInformation: ExposureInformation
        get() = ExposureInformation.ExposureInformationBuilder()
            .setAttenuationValue(Random.nextInt(8))
            .setAttenuationDurations(
                intArrayOf(
                    Random.nextInt(30),
                    Random.nextInt(30),
                    Random.nextInt(30)
                )
            )
            .setDateMillisSinceEpoch(
                Random.nextLong(
                    System.currentTimeMillis(),
                    System.currentTimeMillis() + 66666
                )
            )
            .setDurationMinutes(Random.nextInt(2) * 5)
            .setTotalRiskScore(Random.nextInt(8))
            .setTransmissionRiskLevel(Random.nextInt(8))
            .build()

}