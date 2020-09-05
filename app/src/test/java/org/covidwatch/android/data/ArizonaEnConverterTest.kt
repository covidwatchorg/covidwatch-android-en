package org.covidwatch.android.data

import com.google.android.gms.nearby.exposurenotification.ExposureInformation
import com.google.android.gms.nearby.exposurenotification.TemporaryExposureKey
import org.covidwatch.android.data.model.CovidExposureInformation
import org.covidwatch.android.data.pref.FakePreferenceStorage
import org.covidwatch.android.data.pref.PreferenceStorage
import org.covidwatch.android.exposurenotification.ExposureNotification
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.random.Random

class ArizonaEnConverterTest {
    private val prefs: PreferenceStorage = FakePreferenceStorage()
    private val enConverter = ArizonaEnConverter(prefs)

    private val randomExposure: ExposureInformation.ExposureInformationBuilder
        get() = ExposureInformation.ExposureInformationBuilder()
            .setDateMillisSinceEpoch(System.currentTimeMillis())
            .setDurationMinutes(Random.nextInt(5) * 5)
            .setAttenuationValue(Random.nextInt(8))
            .setTransmissionRiskLevel(Random.nextInt(4))
            .setTotalRiskScore(Random.nextInt(8))
            .setAttenuationDurations(intArrayOf(0, 30, 0))

    private val day0 = Instant.now()
    private val day2 = day0.plus(2, ChronoUnit.DAYS)
    private val day3 = day0.plus(3, ChronoUnit.DAYS)
    private val day4 = day0.plus(4, ChronoUnit.DAYS)
    private val day18 = day0.plus(18, ChronoUnit.DAYS)

    private val day2Ago = day0.plus(-2, ChronoUnit.DAYS)
    private val day3Ago = day0.plus(-3, ChronoUnit.DAYS)
    private val day4Ago = day0.plus(-4, ChronoUnit.DAYS)
    private val day18Ago = day0.plus(-18, ChronoUnit.DAYS)

    private var exposures = listOf(
        CovidExposureInformation(
            attenuationDurations = listOf(5, 10, 5),
            attenuationValue = 0,
            date = day0,
            duration = 0,
            totalRiskScore = 0,
            transmissionRiskLevel = 6,
            id = 0
        ),
        CovidExposureInformation(
            attenuationDurations = listOf(10, 0, 0),
            attenuationValue = 0,
            date = day3,
            duration = 0,
            totalRiskScore = 0,
            transmissionRiskLevel = 6,
            id = 0
        )
    )

    private var exposures3daysAfter = listOf(
        CovidExposureInformation(
            attenuationDurations = listOf(0, 0, 25),
            attenuationValue = 0,
            date = day3,
            duration = 0,
            totalRiskScore = 0,
            transmissionRiskLevel = 6,
            id = 0
        ),
        CovidExposureInformation(
            attenuationDurations = listOf(5, 20, 5),
            attenuationValue = 0,
            date = day3,
            duration = 0,
            totalRiskScore = 0,
            transmissionRiskLevel = 6,
            id = 0
        ),
        CovidExposureInformation(
            attenuationDurations = listOf(5, 0, 0),
            attenuationValue = 0,
            date = day3,
            duration = 0,
            totalRiskScore = 0,
            transmissionRiskLevel = 6,
            id = 0
        )
    )

    private val Instant.intervalNumber: Int
        get() {
            return (toEpochMilli() / ExposureNotification.rollingInterval).toInt()
        }

    private val keyData = ByteArray(42)
    private val keyRollingPeriod = 144

    private val keyBuilder = TemporaryExposureKey.TemporaryExposureKeyBuilder().apply {
        setKeyData(keyData)
        setRollingPeriod(keyRollingPeriod)
    }

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
        assertEquals(1, resultExposure.totalRiskScore)
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
        assertEquals(0, resultExposure.totalRiskScore)
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
        assertEquals(0, resultExposure.totalRiskScore)
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
        assertEquals(0, resultExposure.totalRiskScore)
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
        assertEquals(0, resultExposure.totalRiskScore)
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
        assertEquals(0, resultExposure.totalRiskScore)
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
        assertEquals(0, resultExposure.totalRiskScore)
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
        assertEquals(4, resultExposure.totalRiskScore)
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
        assertEquals(0, resultExposure.totalRiskScore)
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
        assertEquals(1, resultExposure.totalRiskScore)
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
        assertEquals(0, resultExposure.totalRiskScore)
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

    /* RISK LEVEL CALCULATION */

    @Test
    fun `Risk level between two exposures`() {
        //given
        val computeDate = day2
        //when
        val riskLevel = enConverter.riskLevelValue(exposures, computeDate)

        //then
        assertEquals(0.560801, riskLevel, 0.0001)
    }

    @Test
    fun `Risk level at the last exposure`() {
        //given
        val computeDate = day3
        //when
        val riskLevel = enConverter.riskLevelValue(exposures, computeDate)

        //then
        assertEquals(1.270032, riskLevel, 0.0001)
    }

    @Test
    fun `Risk level long after exposures`() {
        //given
        val computeDate = day18
        //when
        val riskLevel = enConverter.riskLevelValue(exposures, computeDate)

        //then
        assertEquals(0.401100, riskLevel, 0.0001)
    }


    @Test
    fun `Risk level before any exposures`() {
        //given
        val computeDate = day2
        //when
        val riskLevel = enConverter.riskLevelValue(exposures3daysAfter, computeDate)

        //then
        assertEquals(0.0, riskLevel, 0.0001)
    }

    @Test
    fun `Risk level one day after exposures`() {
        //given
        val computeDate = day4
        //when
        val riskLevel = enConverter.riskLevelValue(exposures3daysAfter, computeDate)

        //then
        assertEquals(1.161873, riskLevel, 0.0001)
    }

    @Test
    fun `Risk level long after long exposures`() {
        //given
        val computeDate = day18
        //when
        val riskLevel = enConverter.riskLevelValue(exposures3daysAfter, computeDate)

        //then
        assertEquals(0.396128, riskLevel, 0.0001)
    }

    /* DIAGNOSIS KEY CONVERSION */

    @Test
    fun `Key and symptoms dates are the same`() {
        //given
        val daysBetweenKeyAndSymptoms = 0L
        val today = Instant.now()
        val keyDate = Instant.now().plus(daysBetweenKeyAndSymptoms, ChronoUnit.DAYS)
        val intervalNumber = keyDate.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey = enConverter.diagnosisKey(key, today)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(6, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }

    @Test
    fun `Key date is 2 days after symptoms date`() {
        //given
        val daysBetweenKeyAndSymptoms = 2L
        val today = Instant.now()
        val keyDate = Instant.now().plus(daysBetweenKeyAndSymptoms, ChronoUnit.DAYS)
        val intervalNumber = keyDate.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey = enConverter.diagnosisKey(key, today)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(6, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }

    @Test
    fun `Key date is 3 days after symptoms date`() {
        //given
        val daysBetweenKeyAndSymptoms = 3L
        val today = Instant.now()
        val keyDate = Instant.now().plus(daysBetweenKeyAndSymptoms, ChronoUnit.DAYS)
        val intervalNumber = keyDate.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey = enConverter.diagnosisKey(key, today)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(5, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }

    @Test
    fun `Key date is 4 days after symptoms date`() {
        //given
        val daysBetweenKeyAndSymptoms = 4L
        val today = Instant.now()
        val keyDate = Instant.now().plus(daysBetweenKeyAndSymptoms, ChronoUnit.DAYS)
        val intervalNumber = keyDate.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey = enConverter.diagnosisKey(key, today)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(4, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }

    @Test
    fun `Key date is 5 days after symptoms date`() {
        //given
        val daysBetweenKeyAndSymptoms = 5L
        val today = Instant.now()
        val keyDate = Instant.now().plus(daysBetweenKeyAndSymptoms, ChronoUnit.DAYS)
        val intervalNumber = keyDate.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey = enConverter.diagnosisKey(key, today, today, today)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(3, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }

    @Test
    fun `Key date is 6 days after symptoms date`() {
        //given
        val daysBetweenKeyAndSymptoms = 6L
        val today = Instant.now()
        val keyDate = Instant.now().plus(daysBetweenKeyAndSymptoms, ChronoUnit.DAYS)
        val intervalNumber = keyDate.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey = enConverter.diagnosisKey(key, today, today, today)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(2, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }

    @Test
    fun `Key date is 18 days after symptoms date`() {
        //given
        val daysBetweenKeyAndSymptoms = 18L
        val today = Instant.now()
        val keyDate = Instant.now().plus(daysBetweenKeyAndSymptoms, ChronoUnit.DAYS)
        val intervalNumber = keyDate.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey = enConverter.diagnosisKey(key, today, today, today)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(0, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }

    /* Keys before symptoms */
    @Test
    fun `Key date is 2 days before symptoms date`() {
        //given
        val daysBetweenKeyAndSymptoms = -2L
        val today = Instant.now()
        val keyDate = Instant.now().plus(daysBetweenKeyAndSymptoms, ChronoUnit.DAYS)
        val intervalNumber = keyDate.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey = enConverter.diagnosisKey(key, today)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(5, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }

    @Test
    fun `Key date is 3 days before symptoms date`() {
        //given
        val daysBetweenKeyAndSymptoms = -3L
        val today = Instant.now()
        val keyDate = Instant.now().plus(daysBetweenKeyAndSymptoms, ChronoUnit.DAYS)
        val intervalNumber = keyDate.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey = enConverter.diagnosisKey(key, today)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(4, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }

    @Test
    fun `Key date is 4 days before symptoms date`() {
        //given
        val daysBetweenKeyAndSymptoms = -4L
        val today = Instant.now()
        val keyDate = Instant.now().plus(daysBetweenKeyAndSymptoms, ChronoUnit.DAYS)
        val intervalNumber = keyDate.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey = enConverter.diagnosisKey(key, today)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(3, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }

    @Test
    fun `Key date is 5 days before symptoms date`() {
        //given
        val daysBetweenKeyAndSymptoms = -5L
        val today = Instant.now()
        val keyDate = Instant.now().plus(daysBetweenKeyAndSymptoms, ChronoUnit.DAYS)
        val intervalNumber = keyDate.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey = enConverter.diagnosisKey(key, today)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(1, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }

    @Test
    fun `Key date is 18 days before symptoms date`() {
        //given
        val daysBetweenKeyAndSymptoms = -18L
        val today = Instant.now()
        val keyDate = Instant.now().plus(daysBetweenKeyAndSymptoms, ChronoUnit.DAYS)
        val intervalNumber = keyDate.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey = enConverter.diagnosisKey(key, today)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(0, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }

    /* Test date */
    @Test
    fun `Key and test dates are the same`() {
        //given
        val daysBetweenKeyAndSymptoms = 0L
        val today = Instant.now()
        val keyDate = Instant.now().plus(daysBetweenKeyAndSymptoms, ChronoUnit.DAYS)
        val intervalNumber = keyDate.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey = enConverter.diagnosisKey(key, testDate = today)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(3, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }

    @Test
    fun `Key date is 2 days before test date`() {
        //given
        val daysBetweenKeyAndSymptoms = -2L
        val today = Instant.now()
        val keyDate = Instant.now().plus(daysBetweenKeyAndSymptoms, ChronoUnit.DAYS)
        val intervalNumber = keyDate.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey = enConverter.diagnosisKey(key, testDate = today)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(2, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }

    @Test
    fun `Key date is 3 days before test date`() {
        //given
        val daysBetweenKeyAndSymptoms = -3L
        val today = Instant.now()
        val keyDate = Instant.now().plus(daysBetweenKeyAndSymptoms, ChronoUnit.DAYS)
        val intervalNumber = keyDate.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey = enConverter.diagnosisKey(key, testDate = today)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(2, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }

    @Test
    fun `Key date is 18 days before test date`() {
        //given
        val daysBetweenKeyAndSymptoms = -18L
        val today = Instant.now()
        val keyDate = Instant.now().plus(daysBetweenKeyAndSymptoms, ChronoUnit.DAYS)
        val intervalNumber = keyDate.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey = enConverter.diagnosisKey(key, testDate = today)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(0, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }

    @Test
    fun `Key and test dates same but infection date 3 day before key date`() {
        //given
        val intervalNumber = day0.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey =
            enConverter.diagnosisKey(key, testDate = day0, possibleInfectionDate = day3Ago)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(3, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }

    @Test
    fun `Key date 2 days before test date but infection date 4 day before key date`() {
        //given
        val intervalNumber = day0.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey =
            enConverter.diagnosisKey(key, testDate = day2, possibleInfectionDate = day4Ago)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(2, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }

    @Test
    fun `Key date 3 days before test date but infection date 18 day before key date`() {
        //given
        val intervalNumber = day0.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey =
            enConverter.diagnosisKey(key, testDate = day3, possibleInfectionDate = day18Ago)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(2, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }

    @Test
    fun `Key date 18 days before test date`() {
        //given
        val intervalNumber = day0.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey = enConverter.diagnosisKey(key, testDate = day18Ago)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(0, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }

    @Test
    fun `Key date 3 days before test date and 1 day after infection date`() {
        //given
        val intervalNumber = day3Ago.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey =
            enConverter.diagnosisKey(key, testDate = day0, possibleInfectionDate = day4Ago)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(0, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }

    @Test
    fun `Key and infection dates same and 3 days before test date`() {
        //given
        val intervalNumber = day3Ago.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey =
            enConverter.diagnosisKey(key, testDate = day0, possibleInfectionDate = day3Ago)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(0, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }
}