package org.covidwatch.android.data

import com.google.android.gms.nearby.exposurenotification.ExposureInformation
import com.google.android.gms.nearby.exposurenotification.ExposureSummary
import com.google.android.gms.nearby.exposurenotification.TemporaryExposureKey
import com.google.gson.annotations.SerializedName
import java.util.*

// TODO: 20.07.2020 Rename and rework into ExposureRiskModeling interface similarly to iOS
interface EnConverter {
    fun covidExposureSummary(exposureSummary: ExposureSummary): CovidExposureSummary
    fun covidExposureInformation(exposureInformation: ExposureInformation): CovidExposureInformation
    fun diagnosisKey(
        key: TemporaryExposureKey,
        symptomsStartDate: Date?,
        testDate: Date?,
        possibleInfectionDate: Date?
    ): DiagnosisKey

    fun riskLevelValue(exposures: List<CovidExposureInformation>, computeDate: Date): Double
    fun mostRecentSignificantExposureDate(exposures: List<CovidExposureInformation>): Date?
    fun leastRecentSignificantExposureDate(exposures: List<CovidExposureInformation>): Date?
}

open class RiskModelConfiguration(
    /**
     * Exclude days relative to possible infected date
     */
    @SerializedName("excludeDaysRelativeToPossibleInfectedDay")
    val excludeInfectedDays: List<Int>,

    val doseResponseLambda: Double,

    /**
     * According to preliminary dose estimates, the high attenuation distance has a dose 7 times
     * higher than the medium attenuation distance.
     *
     * The mean dose for the low attenuation distance is 0.2 the mean dose of the medium attenuation distance.
     * -AW 6/7/2020
     */
    val attenuationDurationWeights: DoubleArray,

    val significantRiskLevelValueThreshold: Double,

    /**
     * Risk levels for days relative to symptoms start date
     *
     * Key: number of days
     * Value: risk level
     */
    @SerializedName("riskLevelsForDaysRelativeToSymptomsStartDay")
    val riskLevelsSymptomsDate: Map<Int, Int>,

    /**
     * High range shedding ~1010 copies/m3
     * Medium range shedding ~107 copies/m3 (based on estimates of high asymptomatic shedders)
     * Low  range shedding ~104
     *
     * Then assuming between 0.01% and 1% infectivity.
     * Will use copies/m3 since infectivity assumed to apply the same to these concentrations.
     * In the future, we could relate these concentraitons to cycle threshold values of studies
     * to gain more insights into how fractions of infectivity may vary by concentration
     *
     * Transmission risk values increase on a log10 scale, with a 0 transmission level
     * translating to a 0 transmission risk value. These are then multiplied by the time-weighted
     * sum of attenuation. The log10 of this product yields the risk score.
     * Risk scores then translate to risk levels, with assignments described in D2:10 through E2:10.
     * Examples are below.
     */
    val transmissionRiskValuesForLevels: DoubleArray,

    val discountSchedule: List<Double>,

    /**
     * Risk levels for days relative to test date
     *
     * Key: number of days
     * Value: risk level
     */
    @SerializedName("riskLevelsForDaysRelativeToTestDay")
    val riskLevelsTestDate: Map<Int, Int>
)

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

    override fun diagnosisKey(
        key: TemporaryExposureKey,
        symptomsStartDate: Date?,
        testDate: Date?,
        possibleInfectionDate: Date?
    ): DiagnosisKey =
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