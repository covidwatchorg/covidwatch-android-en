package org.covidwatch.android.exposurenotification

import com.google.android.gms.common.api.Api
import com.google.android.gms.common.api.internal.ApiKey
import com.google.android.gms.nearby.exposurenotification.*
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import java.util.*
import kotlin.random.Random

class FakeExposureNotification : ExposureNotificationClient {

    private var started = false

    override fun start(exposureConfiguration: ExposureConfiguration?): Task<Void> {
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

    override fun isEnabled(): Task<Boolean> = Tasks.call { started }

    override fun getTemporaryExposureKeyHistory(): Task<List<TemporaryExposureKey>> =
        Tasks.call {
            List(10) { RandomEnObjects.temporaryExposureKey }
        }

    override fun provideDiagnosisKeys(keys: List<TemporaryExposureKey>): Task<Void> =
        Tasks.call { null }

    override fun getMaxDiagnosisKeyCount(): Task<Int> = Tasks.call { 10 }

    override fun getExposureSummary(): Task<ExposureSummary> =
        Tasks.call { RandomEnObjects.exposureSummary }

    override fun getExposureInformation(): Task<List<ExposureInformation>> =
        Tasks.call {
            List(10) { RandomEnObjects.exposureInformation }
        }

    override fun getApiKey(): ApiKey<Api.ApiOptions.NoOptions> {
        TODO("not implemented")
    }

    override fun resetTemporaryExposureKey(): Task<Void> = Tasks.call {
        null
    }

    override fun resetAllData(): Task<Void> = Tasks.call {
        null
    }
}

object RandomEnObjects {
    val temporaryExposureKey
        get() = TemporaryExposureKey.TemporaryExposureKeyBuilder()
            .setKeyData(ByteArray(42))
            .setRollingStartIntervalNumber(Random.nextInt())
            .setTransmissionRiskLevel(Random.nextInt())
            .build()

    val exposureSummary
        get() = ExposureSummary.ExposureSummaryBuilder()
            .setDaysSinceLastExposure(Random.nextInt())
            .setMatchedKeyCount(Random.nextInt())
            .setMaximumRiskScore(Random.nextInt())
            .build()

    val exposureInformation
        get() = ExposureInformation.ExposureInformationBuilder()
            .setAttenuationValue(Random.nextInt(255))
            .setDateMillisSinceEpoch(Random.nextLong(Date().time, Date().time + 66666))
            .setDurationMinutes(Random.nextInt(50))
            .setTotalRiskScore(Random.nextInt(8))
            .setTransmissionRiskLevel(Random.nextInt(8))
            .build()
}