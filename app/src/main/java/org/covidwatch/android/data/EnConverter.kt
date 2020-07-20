package org.covidwatch.android.data

import com.google.android.gms.nearby.exposurenotification.ExposureInformation
import com.google.android.gms.nearby.exposurenotification.ExposureSummary
import com.google.android.gms.nearby.exposurenotification.TemporaryExposureKey
import java.util.*

// TODO: 20.07.2020 Rename and rework into ExposureRiskModeling interface similarly to iOS
interface EnConverter {
    fun covidExposureSummary(exposureSummary: ExposureSummary): CovidExposureSummary
    fun covidExposureInformation(exposureInformation: ExposureInformation): CovidExposureInformation
    fun diagnosisKey(key: TemporaryExposureKey, symptomsStartDate: Date?): DiagnosisKey
    fun riskLevelValue(exposures: List<CovidExposureInformation>, computeDate: Date): Double
    fun mostRecentSignificantExposureDate(exposures: List<CovidExposureInformation>): Date?
    fun leastRecentSignificantExposureDate(exposures: List<CovidExposureInformation>): Date?
}

@Suppress("unused")
class DefaultEnConverter : EnConverter {
    override fun covidExposureSummary(exposureSummary: ExposureSummary) =
        with(exposureSummary) {
            CovidExposureSummary(
                daysSinceLastExposure,
                matchedKeyCount,
                (maximumRiskScore * 8.0 / 4096).toInt(),
                attenuationDurationsInMinutes,
                (summationRiskScore * 8.0 / 4096).toInt()
            )
        }

    override fun covidExposureInformation(
        exposureInformation: ExposureInformation
    ) =
        with(exposureInformation) {
            CovidExposureInformation(
                date = Date(dateMillisSinceEpoch),
                duration = durationMinutes,
                attenuationValue = attenuationValue,
                transmissionRiskLevel = transmissionRiskLevel,
                totalRiskScore = (totalRiskScore * 8.0 / 4096).toInt(),
                attenuationDurations = attenuationDurationsInMinutes.toList()
            )
        }

    override fun diagnosisKey(key: TemporaryExposureKey, symptomsStartDate: Date?): DiagnosisKey =
        key.asDiagnosisKey().copy(transmissionRisk = 6)

    override fun riskLevelValue(
        exposures: List<CovidExposureInformation>,
        computeDate: Date
    ): Double {
        TODO("not implemented")
    }

    override fun mostRecentSignificantExposureDate(exposures: List<CovidExposureInformation>): Date? {
        TODO("not implemented")
    }

    override fun leastRecentSignificantExposureDate(exposures: List<CovidExposureInformation>): Date? {
        TODO("not implemented")
    }
}