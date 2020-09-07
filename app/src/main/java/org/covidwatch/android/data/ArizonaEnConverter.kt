package org.covidwatch.android.data

import com.google.android.gms.nearby.exposurenotification.ExposureInformation
import com.google.android.gms.nearby.exposurenotification.TemporaryExposureKey
import org.covidwatch.android.data.EnConverter.Companion.DEFAULT_ROLLING_PERIOD
import org.covidwatch.android.data.model.*
import org.covidwatch.android.data.pref.PreferenceStorage
import org.covidwatch.android.exposurenotification.ExposureNotification
import org.covidwatch.android.extension.daysTo
import java.time.Instant
import kotlin.math.exp

class ArizonaEnConverter(private val prefs: PreferenceStorage) : EnConverter {

    private val config: RiskModelConfiguration
        get() = prefs.riskModelConfiguration

    private fun computeAttenuationDurationRiskScore(attenuationDurations: IntArray): Double {
        if (attenuationDurations.size != config.attenuationDurationWeights.size) return 0.0

        return attenuationDurations[0].toDouble() * config.attenuationDurationWeights[0] +
                attenuationDurations[1].toDouble() * config.attenuationDurationWeights[1] +
                attenuationDurations[2].toDouble() * config.attenuationDurationWeights[2]
    }

    private fun computeRiskScore(
        attenuationDurations: IntArray,
        transmissionRiskLevel: Int
    ): RiskScore {
        val transmissionRiskValue =
            config.transmissionRiskValuesForLevels[transmissionRiskLevel]
        val attenuationDurationRiskScore =
            computeAttenuationDurationRiskScore(attenuationDurations)
        val score =
            (1 - exp(-config.doseResponseLambda * transmissionRiskValue * attenuationDurationRiskScore)) * 100
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
        computeDate: Instant
    ): Double {
        var infectedRisk = 0.0
        getDateExposureRisks(exposures).forEach { (date, transmissionRisk) ->
            val days = date.daysTo(computeDate)
            if (days >= 0 && days < config.discountSchedule.size) {
                val discountedRisk = transmissionRisk * config.discountSchedule[days]
                infectedRisk = combineRisks(infectedRisk, discountedRisk)
            }
        }

        return infectedRisk * 100
    }

    override fun mostRecentSignificantExposureDate(exposures: List<CovidExposureInformation>) =
        getDateExposureRisks(exposures)
            .filter { it.value > config.significantRiskLevelValueThreshold }
            .maxBy { it.key }
            ?.key

    override fun leastRecentSignificantExposureDate(exposures: List<CovidExposureInformation>) =
        getDateExposureRisks(exposures)
            .filter { it.value > config.significantRiskLevelValueThreshold }
            .minBy { it.key }
            ?.key

    private fun getDateExposureRisks(exposures: List<CovidExposureInformation>): Map<Instant, Double> {
        val dateExposureRisks = hashMapOf<Instant, Double>()

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

        val transmissionRiskValue = config.transmissionRiskValuesForLevels[transmissionRiskLevel]
        val attenuationDurationRiskScore = computeAttenuationDurationRiskScore(attenuationDurations)
        return 1 - exp(-config.doseResponseLambda * transmissionRiskValue * attenuationDurationRiskScore)
    }

    private fun combineRisks(vararg risks: Double): Double {
        var inverseProduct = 1.0
        risks.forEach { risk ->
            inverseProduct *= (1.0 - risk)
        }
        return (1.0 - inverseProduct)
    }

    private fun Double.within(min: Double, max: Double) = this >= min && this < max

    override fun diagnosisKey(
        key: TemporaryExposureKey,
        symptomsStartDate: Instant?,
        testDate: Instant?,
        possibleInfectionDate: Instant?
    ): DiagnosisKey {
        val keyDate =
            Instant.ofEpochMilli(key.rollingStartIntervalNumber * ExposureNotification.rollingInterval)

        val transmissionRisk = when {
            symptomsStartDate != null -> {
                val days = symptomsStartDate.daysTo(keyDate)

                config.riskLevelsSymptomsDate[days] ?: 0
            }
            testDate != null -> {
                val days = testDate.daysTo(keyDate)
                val risk = config.riskLevelsTestDate[days] ?: 0

                if (possibleInfectionDate == null) risk
                else {
                    val relativeInfectionDay = possibleInfectionDate.daysTo(keyDate)
                    if (config.excludeInfectedDays.contains(relativeInfectionDay)) 0
                    else risk
                }
            }
            else -> 0
        }

        return key.asDiagnosisKey().copy(
            transmissionRisk = transmissionRisk,
            rollingPeriod = if (key.rollingPeriod == 0) DEFAULT_ROLLING_PERIOD else key.rollingPeriod
        )
    }

    override fun riskMetrics(
        exposures: List<CovidExposureInformation>,
        computeDate: Instant
    ) = RiskMetrics(
        riskLevelValue(exposures, computeDate),
        leastRecentSignificantExposureDate(exposures),
        mostRecentSignificantExposureDate(exposures)
    )

    override fun covidExposureInformation(
        exposureInformation: ExposureInformation
    ) = with(exposureInformation) {
        CovidExposureInformation(
            date = Instant.ofEpochMilli(dateMillisSinceEpoch),
            duration = durationMinutes,
            attenuationValue = attenuationValue,
            transmissionRiskLevel = transmissionRiskLevel,
            totalRiskScore = computeRiskScore(
                attenuationDurationsInMinutes,
                transmissionRiskLevel
            ),
            attenuationDurations = attenuationDurationsInMinutes.toList(),
            id = dateMillisSinceEpoch
        )
    }
}

class ArizonaRiskModelConfiguration : RiskModelConfiguration(
    excludeInfectedDays = listOf(0, 1),
    doseResponseLambda = 0.0000031,
    attenuationDurationWeights = doubleArrayOf(
        2.39, // Low attenuation / close proximity
        0.6,  // Medium attenuation / medium proximity
        0.06  // high attenuation / far proximity
    ),
    significantRiskLevelValueThreshold = 0.01,
    riskLevelsSymptomsDate = mapOf(
        Pair(-5, 1),
        Pair(-4, 3),
        Pair(-3, 4),
        Pair(-2, 5),
        Pair(-1, 6),
        Pair(0, 6),
        Pair(1, 6),
        Pair(2, 6),
        Pair(3, 5),
        Pair(4, 4),
        Pair(5, 3),
        Pair(6, 2),
        Pair(7, 2),
        Pair(8, 1),
        Pair(9, 1)
    ),
    transmissionRiskValuesForLevels = doubleArrayOf(
        0.0, // Level 0
        10.0, // Level 1
        21.5443469, // Level 2
        31.6227766, // Level 3
        46.4158883, // Level 4
        68.1292069, // Level 5
        100.0, // Level 6
        100.0 // Level 7 (unused)
    ),
    discountSchedule = listOf(
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
    ),
    riskLevelsTestDate = mapOf(
        Pair(-4, 2),
        Pair(-3, 2),
        Pair(-2, 2),
        Pair(-1, 3),
        Pair(0, 3),
        Pair(1, 3),
        Pair(2, 2),
        Pair(3, 2),
        Pair(4, 2)
    )
)
