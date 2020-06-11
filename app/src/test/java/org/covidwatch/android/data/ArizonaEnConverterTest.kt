package org.covidwatch.android.data

import com.google.android.gms.nearby.exposurenotification.ExposureInformation
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.test.assertEquals

class ArizonaEnConverterTest {
    private val enConverter = ArizonaEnConverter()

    private val randomExposure: ExposureInformation.ExposureInformationBuilder
        get() = ExposureInformation.ExposureInformationBuilder()
            .setDateMillisSinceEpoch(System.currentTimeMillis())
            .setDurationMinutes(Random.nextInt(10) * 5)
            .setAttenuationValue(Random.nextInt(8))
            .setTransmissionRiskLevel(Random.nextInt(4))
            .setTotalRiskScore(Random.nextInt(8))
            .setAttenuationDurations(intArrayOf(0, 30 * 60, 0))

    @Test
    fun `Sufficiently risky individual, 30 minutes at 6 ft`() {
        //given
        val testExposure = randomExposure
            .setAttenuationDurations(intArrayOf(0, 30 * 60, 0))
            .setTransmissionRiskLevel(4)
            .build()

        //when
        val resultExposure = enConverter.covidExposureInformation(testExposure)

        //then
        assertEquals(5, resultExposure.totalRiskScore)
    }

    @Test
    fun `Sufficiently risky individual, 5 minutes close contact`() {
        //given
        val testExposure = randomExposure
            .setAttenuationDurations(intArrayOf(15 * 60, 0, 0))
            .setTransmissionRiskLevel(4)
            .build()

        //when
        val resultExposure = enConverter.covidExposureInformation(testExposure)

        //then
        assertEquals(6, resultExposure.totalRiskScore)
    }

    @Test
    fun `Highest risk individual, 30 minutes at 6 ft`() {
        //given
        val testExposure = randomExposure
            .setAttenuationDurations(intArrayOf(0, 30 * 60, 0))
            .setTransmissionRiskLevel(6)
            .build()

        //when
        val resultExposure = enConverter.covidExposureInformation(testExposure)

        //then
        assertEquals(8, resultExposure.totalRiskScore)
    }

    @Test
    fun `Highest risk individual, 5 minutes close contact`() {
        //given
        val testExposure = randomExposure
            .setAttenuationDurations(intArrayOf(5 * 60, 0, 0))
            .setTransmissionRiskLevel(6)
            .build()

        //when
        val resultExposure = enConverter.covidExposureInformation(testExposure)

        //then
        assertEquals(8, resultExposure.totalRiskScore)
    }

    @Test
    fun `Highest risk individual, 5 minutes at 6 ft`() {
        //given
        val testExposure = randomExposure
            .setAttenuationDurations(intArrayOf(0, 5 * 60, 0))
            .setTransmissionRiskLevel(6)
            .build()

        //when
        val resultExposure = enConverter.covidExposureInformation(testExposure)

        //then
        assertEquals(7, resultExposure.totalRiskScore)
    }

    @Test
    fun `Highest risk individual, 30 minutes at long distance`() {
        //given
        val testExposure = randomExposure
            .setAttenuationDurations(intArrayOf(0, 0, 30 * 60))
            .setTransmissionRiskLevel(6)
            .build()

        //when
        val resultExposure = enConverter.covidExposureInformation(testExposure)

        //then
        assertEquals(4, resultExposure.totalRiskScore)
    }

    @Test
    fun `Asymptomatic shedder at peak risk, 30 min at 6 ft`() {
        //given
        val testExposure = randomExposure
            .setAttenuationDurations(intArrayOf(0, 30 * 60, 0))
            .setTransmissionRiskLevel(3)
            .build()

        //when
        val resultExposure = enConverter.covidExposureInformation(testExposure)

        //then
        assertEquals(4, resultExposure.totalRiskScore)
    }

    @Test
    fun `Low shedder, 30 min at 6 ft`() {
        //given
        val testExposure = randomExposure
            .setAttenuationDurations(intArrayOf(0, 30 * 60, 0))
            .setTransmissionRiskLevel(2)
            .build()

        //when
        val resultExposure = enConverter.covidExposureInformation(testExposure)

        //then
        assertEquals(2, resultExposure.totalRiskScore)
    }

    @Test
    fun `Low shedder, 5 min at 6 ft`() {
        //given
        val testExposure = randomExposure
            .setAttenuationDurations(intArrayOf(0, 5 * 60, 0))
            .setTransmissionRiskLevel(2)
            .build()

        //when
        val resultExposure = enConverter.covidExposureInformation(testExposure)

        //then
        assertEquals(1, resultExposure.totalRiskScore)
    }
}