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
            .setDurationMinutes(Random.nextInt(5) * 5)
            .setAttenuationValue(Random.nextInt(8))
            .setTransmissionRiskLevel(Random.nextInt(4))
            .setTotalRiskScore(Random.nextInt(8))
            .setAttenuationDurations(intArrayOf(0, 30, 0))

    @Test
    fun `Sufficiently risky individual, 30 minutes at close contact`() {
        //given
        val testExposure = randomExposure
            .setAttenuationDurations(intArrayOf(30, 0, 0))
            .setTransmissionRiskLevel(4)
            .build()

        //when
        val resultExposure = enConverter.covidExposureInformation(testExposure)

        //then
        assertEquals(8, resultExposure.totalRiskScore)
    }

    @Test
    fun `Sufficiently risky individual, 30 minutes at medium attenuation`() {
        //given
        val testExposure = randomExposure
            .setAttenuationDurations(intArrayOf(0, 30, 0))
            .setTransmissionRiskLevel(4)
            .build()

        //when
        val resultExposure = enConverter.covidExposureInformation(testExposure)

        //then
        assertEquals(4, resultExposure.totalRiskScore)
    }

    @Test
    fun `Sufficiently risky individual, 5 minutes close contact`() {
        //given
        val testExposure = randomExposure
            .setAttenuationDurations(intArrayOf(5, 0, 0))
            .setTransmissionRiskLevel(4)
            .build()

        //when
        val resultExposure = enConverter.covidExposureInformation(testExposure)

        //then
        assertEquals(0, resultExposure.totalRiskScore)
    }

    @Test
    fun `Highest risk individual, 30 minutes at medium attenuation`() {
        //given
        val testExposure = randomExposure
            .setAttenuationDurations(intArrayOf(0, 30, 0))
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
            .setAttenuationDurations(intArrayOf(5, 0, 0))
            .setTransmissionRiskLevel(6)
            .build()

        //when
        val resultExposure = enConverter.covidExposureInformation(testExposure)

        //then
        assertEquals(2, resultExposure.totalRiskScore)
    }

    @Test
    fun `Highest risk individual, 5 minutes at medium attenuation`() {
        //given
        val testExposure = randomExposure
            .setAttenuationDurations(intArrayOf(0, 5, 0))
            .setTransmissionRiskLevel(6)
            .build()

        //when
        val resultExposure = enConverter.covidExposureInformation(testExposure)

        //then
        assertEquals(0, resultExposure.totalRiskScore)
    }

    @Test
    fun `Highest risk individual, 30 minutes at long distance`() {
        //given
        val testExposure = randomExposure
            .setAttenuationDurations(intArrayOf(0, 0, 30))
            .setTransmissionRiskLevel(6)
            .build()

        //when
        val resultExposure = enConverter.covidExposureInformation(testExposure)

        //then
        assertEquals(5, resultExposure.totalRiskScore)
    }

    @Test
    fun `Asymptomatic shedder at peak risk, 30 min at medium attenuation`() {
        //given
        val testExposure = randomExposure
            .setAttenuationDurations(intArrayOf(0, 30, 0))
            .setTransmissionRiskLevel(3)
            .build()

        //when
        val resultExposure = enConverter.covidExposureInformation(testExposure)

        //then
        assertEquals(2, resultExposure.totalRiskScore)
    }

    @Test
    fun `Low shedder, 30 min at medium attenuation`() {
        //given
        val testExposure = randomExposure
            .setAttenuationDurations(intArrayOf(0, 30, 0))
            .setTransmissionRiskLevel(2)
            .build()

        //when
        val resultExposure = enConverter.covidExposureInformation(testExposure)

        //then
        assertEquals(1, resultExposure.totalRiskScore)
    }

    @Test
    fun `Low shedder, 5 min at medium attenuation`() {
        //given
        val testExposure = randomExposure
            .setAttenuationDurations(intArrayOf(0, 5, 0))
            .setTransmissionRiskLevel(2)
            .build()

        //when
        val resultExposure = enConverter.covidExposureInformation(testExposure)

        //then
        assertEquals(0, resultExposure.totalRiskScore)
    }

    @Test
    fun `Highest risk individual, 30 min in each bucket`() {
        //given
        val testExposure = randomExposure
            .setAttenuationDurations(intArrayOf(30, 30, 30))
            .setTransmissionRiskLevel(6)
            .build()

        //when
        val resultExposure = enConverter.covidExposureInformation(testExposure)

        //then
        assertEquals(8, resultExposure.totalRiskScore)
    }

    @Test
    fun `Lowest risk individual, 30 min in each bucket`() {
        //given
        val testExposure = randomExposure
            .setAttenuationDurations(intArrayOf(30, 30, 30))
            .setTransmissionRiskLevel(1)
            .build()

        //when
        val resultExposure = enConverter.covidExposureInformation(testExposure)

        //then
        assertEquals(2, resultExposure.totalRiskScore)
    }

    @Test
    fun `Highest risk individual 15 minutes close contact`() {
        //given
        val testExposure = randomExposure
            .setAttenuationDurations(intArrayOf(15, 15, 15))
            .setTransmissionRiskLevel(6)
            .build()

        //when
        val resultExposure = enConverter.covidExposureInformation(testExposure)

        //then
        assertEquals(8, resultExposure.totalRiskScore)
    }

    @Test
    fun `Lowest risk individual 15 minutes close contact`() {
        //given
        val testExposure = randomExposure
            .setAttenuationDurations(intArrayOf(15, 15, 15))
            .setTransmissionRiskLevel(1)
            .build()

        //when
        val resultExposure = enConverter.covidExposureInformation(testExposure)

        //then
        assertEquals(0, resultExposure.totalRiskScore)
    }

    @Test
    fun `Highest risk individual 15 minutes long distance`() {
        //given
        val testExposure = randomExposure
            .setAttenuationDurations(intArrayOf(0, 0, 15))
            .setTransmissionRiskLevel(6)
            .build()

        //when
        val resultExposure = enConverter.covidExposureInformation(testExposure)

        //then
        assertEquals(2, resultExposure.totalRiskScore)
    }

    @Test
    fun `Lowest risk individual 15 minutes long distance`() {
        //given
        val testExposure = randomExposure
            .setAttenuationDurations(intArrayOf(0, 0, 15))
            .setTransmissionRiskLevel(1)
            .build()

        //when
        val resultExposure = enConverter.covidExposureInformation(testExposure)

        //then
        assertEquals(0, resultExposure.totalRiskScore)
    }
}