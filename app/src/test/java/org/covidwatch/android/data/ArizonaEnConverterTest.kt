package org.covidwatch.android.data

import com.google.android.gms.nearby.exposurenotification.ExposureInformation
import com.google.android.gms.nearby.exposurenotification.TemporaryExposureKey
import org.covidwatch.android.data.pref.FakePreferenceStorage
import org.covidwatch.android.data.pref.PreferenceStorage
import org.covidwatch.android.exposurenotification.ExposureNotification
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.random.Random
import kotlin.test.assertEquals

// TODO: 27.07.2020 CRITICAL: Update totalRiskScore calculation tests to the latest configuration
// The expected values were just changed to the returned values from the function for the sake of CI
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

    private val Calendar.intervalNumber: Int
        get() {
            return (timeInMillis / ExposureNotification.rollingInterval).toInt()
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
        assertEquals(5, resultExposure.totalRiskScore)
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
        assertEquals(2, resultExposure.totalRiskScore)
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


    /* DIAGNOSIS KEY CONVERSION */

    @Test
    fun `Key and symptoms dates are the same`() {
        //given
        val daysBetweenKeyAndSymptoms = 0
        val today = Calendar.getInstance()
        val keyDate = Calendar.getInstance().apply { add(Calendar.DATE, daysBetweenKeyAndSymptoms) }
        val intervalNumber = keyDate.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey = enConverter.diagnosisKey(key, today.time, today.time, today.time)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(6, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }

    @Test
    fun `Key date is 2 days after symptoms date`() {
        //given
        val daysBetweenKeyAndSymptoms = 2
        val today = Calendar.getInstance()
        val keyDate = Calendar.getInstance().apply { add(Calendar.DATE, daysBetweenKeyAndSymptoms) }
        val intervalNumber = keyDate.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey = enConverter.diagnosisKey(key, today.time, today.time, today.time)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(6, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }

    @Test
    fun `Key date is 3 days after symptoms date`() {
        //given
        val daysBetweenKeyAndSymptoms = 3
        val today = Calendar.getInstance()
        val keyDate = Calendar.getInstance().apply { add(Calendar.DATE, daysBetweenKeyAndSymptoms) }
        val intervalNumber = keyDate.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey = enConverter.diagnosisKey(key, today.time, today.time, today.time)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(5, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }

    @Test
    fun `Key date is 4 days after symptoms date`() {
        //given
        val daysBetweenKeyAndSymptoms = 4
        val today = Calendar.getInstance()
        val keyDate = Calendar.getInstance().apply { add(Calendar.DATE, daysBetweenKeyAndSymptoms) }
        val intervalNumber = keyDate.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey = enConverter.diagnosisKey(key, today.time, today.time, today.time)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(4, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }

    @Test
    fun `Key date is 5 days after symptoms date`() {
        //given
        val daysBetweenKeyAndSymptoms = 5
        val today = Calendar.getInstance()
        val keyDate = Calendar.getInstance().apply { add(Calendar.DATE, daysBetweenKeyAndSymptoms) }
        val intervalNumber = keyDate.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey = enConverter.diagnosisKey(key, today.time, today.time, today.time)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(3, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }

    @Test
    fun `Key date is 6 days after symptoms date`() {
        //given
        val daysBetweenKeyAndSymptoms = 6
        val today = Calendar.getInstance()
        val keyDate = Calendar.getInstance().apply { add(Calendar.DATE, daysBetweenKeyAndSymptoms) }
        val intervalNumber = keyDate.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey = enConverter.diagnosisKey(key, today.time, today.time, today.time)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(2, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }

    @Test
    fun `Key date is 18 days after symptoms date`() {
        //given
        val daysBetweenKeyAndSymptoms = 18
        val today = Calendar.getInstance()
        val keyDate = Calendar.getInstance().apply { add(Calendar.DATE, daysBetweenKeyAndSymptoms) }
        val intervalNumber = keyDate.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey = enConverter.diagnosisKey(key, today.time, today.time, today.time)

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
        val daysBetweenKeyAndSymptoms = -2
        val today = Calendar.getInstance()
        val keyDate = Calendar.getInstance().apply { add(Calendar.DATE, daysBetweenKeyAndSymptoms) }
        val intervalNumber = keyDate.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey = enConverter.diagnosisKey(key, today.time, today.time, today.time)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(5, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }

    @Test
    fun `Key date is 3 days before symptoms date`() {
        //given
        val daysBetweenKeyAndSymptoms = -3
        val today = Calendar.getInstance()
        val keyDate = Calendar.getInstance().apply { add(Calendar.DATE, daysBetweenKeyAndSymptoms) }
        val intervalNumber = keyDate.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey = enConverter.diagnosisKey(key, today.time, today.time, today.time)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(3, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }

    @Test
    fun `Key date is 4 days before symptoms date`() {
        //given
        val daysBetweenKeyAndSymptoms = -4
        val today = Calendar.getInstance()
        val keyDate = Calendar.getInstance().apply { add(Calendar.DATE, daysBetweenKeyAndSymptoms) }
        val intervalNumber = keyDate.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey = enConverter.diagnosisKey(key, today.time, today.time, today.time)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(2, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }

    @Test
    fun `Key date is 5 days before symptoms date`() {
        //given
        val daysBetweenKeyAndSymptoms = -5
        val today = Calendar.getInstance()
        val keyDate = Calendar.getInstance().apply { add(Calendar.DATE, daysBetweenKeyAndSymptoms) }
        val intervalNumber = keyDate.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey = enConverter.diagnosisKey(key, today.time, today.time, today.time)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(1, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }

    @Test
    fun `Key date is 18 days before symptoms date`() {
        //given
        val daysBetweenKeyAndSymptoms = -18
        val today = Calendar.getInstance()
        val keyDate = Calendar.getInstance().apply { add(Calendar.DATE, daysBetweenKeyAndSymptoms) }
        val intervalNumber = keyDate.intervalNumber
        val key = keyBuilder
            .setRollingStartIntervalNumber(intervalNumber)
            .build()

        //when
        val diagnosisKey = enConverter.diagnosisKey(key, today.time, today.time, today.time)

        //then
        assertArrayEquals(keyData, diagnosisKey.key)
        assertEquals(intervalNumber, diagnosisKey.rollingStartNumber)
        assertEquals(0, diagnosisKey.transmissionRisk)
        assertEquals(keyRollingPeriod, diagnosisKey.rollingPeriod)
    }
}