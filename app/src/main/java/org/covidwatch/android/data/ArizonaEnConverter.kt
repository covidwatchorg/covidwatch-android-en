package org.covidwatch.android.data

import com.google.android.gms.nearby.exposurenotification.ExposureInformation
import com.google.android.gms.nearby.exposurenotification.ExposureSummary
import com.google.android.gms.nearby.exposurenotification.TemporaryExposureKey
import org.covidwatch.android.exposurenotification.ExposureNotification
import org.covidwatch.android.extension.toLocalDate
import java.time.Period
import java.util.*
import kotlin.math.abs
import kotlin.math.exp

class ArizonaEnConverter : EnConverter {

    var significantRiskLevelValueThreshold = 0.011

    private val discountSchedule = listOf(
        1.0,
        0.99998,
        0.994059,
        0.9497885,
        0.858806,
        0.755134,
        0.660103392,
        0.586894919,
        0.533407703,
        0.494373128,
        0.463039432,
        0.438587189,
        0.416241392,
        0.393207216,
        0.367287169,
        0.340932595,
        0.313997176,
        0.286927378,
        0.265554932,
        0.240765331,
        0.217746365,
        0.201059905,
        0.185435372,
        0.172969757,
        0.156689676,
        0.141405162,
        0.124388311,
        0.108319101,
        0.094752304,
        0.081300662,
        0.070016527,
        0.056302622,
        0.044703284,
        0.036214683,
        0.030309399,
        0.024554527,
        0.018833743,
        0.014769669
    )

    /**
     * According to preliminary dose estimates, the high attenuation distance has a dose 7 times
     * higher than the medium attenuation distance.
     *
     * The mean dose for the low attenuation distance is 0.2 the mean dose of the medium attenuation distance.
     * -AW 6/7/2020
     */
    private val attenuationDurationWeights = doubleArrayOf(
        2.0182978, // High attenuation: D < 0.5m
        1.1507629, // Medium attenuation: 0.5m < D < 2m
        0.6651614 // Low attenuation: 2m < D
    )

    private val doseResponseLambda = 1.71E-05

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
    private val transmissionRiskValuesForLevels = doubleArrayOf(
        0.0, // Level 0
        10.0, // Level 1
        21.5443469, // Level 2
        31.6227766, // Level 3
        46.4158883, // Level 4
        68.1292069, // Level 5
        100.0, // Level 6
        100.0 // Level 7 (unused)
    )

    /**
     * Risk levels for days including and before symptoms start day
     */
    private val riskLevelsBeforeSymptoms = listOf(
        6, 6, 5, 3, 2, 1, 1
    )

    /**
     * Risk levels for days including and after symptoms start day
     */
    private val riskLevelsAfterSymptoms = listOf(
        6, 6, 6, 5, 4, 3, 2, 1, 1, 1, 1, 1
    )

    private fun computeAttenuationDurationRiskScore(attenuationDurations: IntArray): Double {
        if (attenuationDurations.size != attenuationDurationWeights.size) return 0.0

        return attenuationDurations[0].toDouble() * attenuationDurationWeights[0] +
                attenuationDurations[1].toDouble() * attenuationDurationWeights[1] +
                attenuationDurations[2].toDouble() * attenuationDurationWeights[2]
    }

    private fun computeRiskScore(
        attenuationDurations: IntArray,
        transmissionRiskLevel: Int
    ): RiskScore {
        val transmissionRiskValue = transmissionRiskValuesForLevels[transmissionRiskLevel]
        val attenuationDurationRiskScore = computeAttenuationDurationRiskScore(attenuationDurations)
        val score =
            (1 - exp(-doseResponseLambda * transmissionRiskValue * attenuationDurationRiskScore)) * 100
        return when {
            score.within(Double.MIN_VALUE, 1.0) -> 0
            score.within(1.0, 1.5) -> 1
            score.within(1.5, 2.0) -> 2
            score.within(2.0, 2.5) -> 3
            score.within(2.5, 3.0) -> 4
            score.within(3.0, 3.5) -> 5
            score.within(3.5, 4.0) -> 6
            score.within(4.0, 4.5) -> 7
            else -> 8
        }
    }

    override fun riskLevelValue(
        exposures: List<CovidExposureInformation>,
        computeDate: Date
    ): Double {
        var infectedRisk = 0.0
        getDateExposureRisks(exposures).forEach { (date, transmissionRisk) ->
            val days = Period.between(date.toLocalDate(), computeDate.toLocalDate()).days
            if (days >= 0 && days < discountSchedule.size) {
                val discountedRisk = transmissionRisk * discountSchedule[days]
                infectedRisk = combineRisks(infectedRisk, discountedRisk)
            }
        }

        return infectedRisk * 100
    }

    override fun mostRecentSignificantExposureDate(exposures: List<CovidExposureInformation>) =
        getDateExposureRisks(exposures)
            .filter { it.value > significantRiskLevelValueThreshold }
            .maxBy { it.key }
            ?.key

    override fun leastRecentSignificantExposureDate(exposures: List<CovidExposureInformation>) =
        getDateExposureRisks(exposures)
            .filter { it.value > significantRiskLevelValueThreshold }
            .minBy { it.key }
            ?.key

    private fun getDateExposureRisks(exposures: List<CovidExposureInformation>): Map<Date, Double> {
        val dateExposureRisks = hashMapOf<Date, Double>()

        exposures.forEach { exposure ->
            val date = exposure.date
            val newRisk = computeRisk(exposure)
            val prevRisk = dateExposureRisks[date]
            if (prevRisk == null) {
                dateExposureRisks[date] = newRisk
            } else {
                dateExposureRisks[date] = combineRisks(newRisk, prevRisk)
            }
        }
        return dateExposureRisks
    }

    private fun computeRisk(exposure: CovidExposureInformation): Double {
        val transmissionRiskLevel = exposure.transmissionRiskLevel
        val attenuationDurations = exposure.attenuationDurations.toIntArray()

        val transmissionRiskValue = transmissionRiskValuesForLevels[transmissionRiskLevel]
        val attenuationDurationRiskScore = computeAttenuationDurationRiskScore(attenuationDurations)
        return 1 - exp(-doseResponseLambda * transmissionRiskValue * attenuationDurationRiskScore)
    }

    private fun combineRisks(vararg risks: Double): Double {
        var inverseProduct = 1.0
        risks.forEach { risk ->
            inverseProduct *= (1.0 - risk)
        }
        return (1.0 - inverseProduct)
    }

    private fun Double.within(min: Double, max: Double) = this >= min && this < max

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

    override fun diagnosisKey(key: TemporaryExposureKey, symptomsStartDate: Date?): DiagnosisKey {
        symptomsStartDate ?: return key.asDiagnosisKey()

        val keyDate =
            Date(key.rollingStartIntervalNumber * ExposureNotification.rollingInterval).toLocalDate()

        val days = Period.between(symptomsStartDate.toLocalDate(), keyDate).days
        val absDays = abs(days)

        val transmissionRisk = when {
            days <= 0 && absDays < riskLevelsBeforeSymptoms.count() -> riskLevelsBeforeSymptoms[absDays]
            absDays < riskLevelsAfterSymptoms.count() -> riskLevelsAfterSymptoms[absDays]
            else -> 0
        }
        return key.asDiagnosisKey().copy(transmissionRisk = transmissionRisk)
    }

    override fun covidExposureInformation(
        exposureInformation: ExposureInformation
    ) = with(exposureInformation) {
        CovidExposureInformation(
            date = Date(dateMillisSinceEpoch),
            duration = durationMinutes,
            attenuationValue = attenuationValue,
            transmissionRiskLevel = transmissionRiskLevel,
            totalRiskScore = computeRiskScore(
                attenuationDurationsInMinutes,
                transmissionRiskLevel
            ),
            attenuationDurations = attenuationDurationsInMinutes.toList()
        )
    }
}