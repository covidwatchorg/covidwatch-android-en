package org.covidwatch.android.data

import com.google.android.gms.nearby.exposurenotification.ExposureInformation
import com.google.android.gms.nearby.exposurenotification.ExposureSummary
import java.util.*
import kotlin.math.log10

class ArizonaEnConverter : EnConverter {

    /**
     * According to preliminary dose estimates, the high attenuation distance has a dose 7 times
     * higher than the medium attenuation distance.
     *
     * The mean dose for the low attenuation distance is 0.2 the mean dose of the medium attenuation distance.
     * -AW 6/7/2020
     */
    private val attenuationDurationWeights = doubleArrayOf(
        7.0, // High attenuation: D < 0.5m
        1.0, // Medium attenuation: 0.5m < D < 2m
        0.0002 // Low attenuation: 2m < D
    )

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
        0.00E+00, // Level 0
        1.00E+03, // Level 1
        1.00E+04, // Level 2
        1.00E+06, // Level 3
        1.00E+07, // Level 4
        1.00E+09, // Level 5
        1.00E+10, // Level 6
        1.00E+10  // Level 7 (unused)
    )

    private fun computeAttenuationDurationRiskScore(attenuationDurations: IntArray): Double {
        if (attenuationDurations.size != attenuationDurationWeights.size) return 0.0

        return attenuationDurations[0].toDouble() / 60 * attenuationDurationWeights[0] +
                attenuationDurations[1].toDouble() / 60 * attenuationDurationWeights[1] +
                attenuationDurations[2].toDouble() / 60 * attenuationDurationWeights[2]
    }

    private fun computeRiskScore(
        attenuationDurations: IntArray,
        transmissionRiskLevel: Int
    ): RiskScore {
        val transmissionRiskValue = transmissionRiskValuesForLevels[transmissionRiskLevel]
        val attenuationDurationRiskScore = computeAttenuationDurationRiskScore(attenuationDurations)

        return when (log10(transmissionRiskValue * attenuationDurationRiskScore).toInt()) {
            in Int.MIN_VALUE until 3 -> 0
            in 3 until 5 -> 1
            in 5 until 6 -> 2
            in 6 until 7 -> 3
            in 7 until 8 -> 4
            in 8 until 9 -> 5
            in 9 until 10 -> 6
            in 10 until 11 -> 7
            else -> 8
        }
    }

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

    override fun covidExposureInformation(exposureInformation: ExposureInformation) =
        with(exposureInformation) {
            CovidExposureInformation(
                date = Date(dateMillisSinceEpoch),
                durationMinutes = durationMinutes,
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