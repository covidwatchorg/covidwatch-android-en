package org.covidwatch.android.data

import com.google.android.gms.nearby.exposurenotification.ExposureInformation
import com.google.android.gms.nearby.exposurenotification.TemporaryExposureKey
import org.covidwatch.android.data.model.CovidExposureInformation
import org.covidwatch.android.data.model.DiagnosisKey
import org.covidwatch.android.data.model.RiskMetrics
import org.covidwatch.android.data.model.asDiagnosisKey
import java.time.Instant

// TODO: 20.07.2020 Rename and rework into ExposureRiskModeling interface similarly to iOS
interface EnConverter {
    fun covidExposureInformation(exposureInformation: ExposureInformation): CovidExposureInformation
    fun diagnosisKey(
        key: TemporaryExposureKey,
        symptomsStartDate: Instant? = null,
        testDate: Instant? = null,
        possibleInfectionDate: Instant? = null
    ): DiagnosisKey

    fun riskLevelValue(exposures: List<CovidExposureInformation>, computeDate: Instant): Double
    fun mostRecentSignificantExposureDate(exposures: List<CovidExposureInformation>): Instant?
    fun leastRecentSignificantExposureDate(exposures: List<CovidExposureInformation>): Instant?
    fun riskMetrics(exposures: List<CovidExposureInformation>, computeDate: Instant): RiskMetrics

    companion object {
        const val DEFAULT_ROLLING_PERIOD = 144
    }
}

@Suppress("unused")
class DefaultEnConverter : EnConverter {

    override fun covidExposureInformation(
        exposureInformation: ExposureInformation
    ) =
        with(exposureInformation) {
            CovidExposureInformation(
                date = Instant.ofEpochMilli(dateMillisSinceEpoch),
                duration = durationMinutes,
                attenuationValue = attenuationValue,
                transmissionRiskLevel = transmissionRiskLevel,
                totalRiskScore = (totalRiskScore * 8.0 / 4096).toInt(),
                attenuationDurations = attenuationDurationsInMinutes.toList(),
                id = dateMillisSinceEpoch
            )
        }

    override fun diagnosisKey(
        key: TemporaryExposureKey,
        symptomsStartDate: Instant?,
        testDate: Instant?,
        possibleInfectionDate: Instant?
    ): DiagnosisKey =
        key.asDiagnosisKey().copy(transmissionRisk = 6)

    override fun riskLevelValue(
        exposures: List<CovidExposureInformation>,
        computeDate: Instant
    ): Double {
        TODO("not implemented")
    }

    override fun mostRecentSignificantExposureDate(exposures: List<CovidExposureInformation>): Instant? {
        TODO("not implemented")
    }

    override fun leastRecentSignificantExposureDate(exposures: List<CovidExposureInformation>): Instant? {
        TODO("not implemented")
    }

    override fun riskMetrics(
        exposures: List<CovidExposureInformation>,
        computeDate: Instant
    ): RiskMetrics {
        TODO("not implemented")
    }
}