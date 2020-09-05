package org.covidwatch.android.data.model

import com.google.gson.annotations.SerializedName

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