package org.covidwatch.android.ui.home

import com.android.example.livedatabuilder.util.getOrAwaitValue
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.covidwatch.android.InstantExecutorExtension
import org.covidwatch.android.data.UserFlowRepository
import org.covidwatch.android.data.model.NextStep
import org.covidwatch.android.data.model.NextStepType
import org.covidwatch.android.data.model.RiskLevel
import org.covidwatch.android.data.model.RiskMetrics
import org.covidwatch.android.data.pref.PreferenceStorage
import org.covidwatch.android.data.risklevel.RiskLevelRepository
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.extension.toLocalDate
import org.covidwatch.android.ui.util.DateFormatter
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import kotlin.test.assertEquals

@ExtendWith(InstantExecutorExtension::class)
internal class HomeViewModelTest {
    private val enManager: ExposureNotificationManager = mockk()
    private val userFlowRepository: UserFlowRepository = mockk()
    private val preferences: PreferenceStorage = mockk()

    private val friday =
        LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.FRIDAY)).atStartOfDay()
            .toInstant(ZoneOffset.UTC)
    private val saturday =
        LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SATURDAY)).atStartOfDay()
            .toInstant(ZoneOffset.UTC)
    private val sunday =
        LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).atStartOfDay()
            .toInstant(ZoneOffset.UTC)

    abstract class FakeRiskLevelRepository : RiskLevelRepository {
        override val riskLevel: Flow<RiskLevel>
            get() = TODO("not implemented")
    }

    private lateinit var viewModel: HomeViewModel

    @Test
    fun `Latest exposure 5 days after without weekend adjusting`() {
        testDateFlagsReplacement(
            exposureType = LATEST_EXPOSURE,
            daysOffset = 5L,
            oldestExposure = Instant.now().minus(5, ChronoUnit.DAYS),
            newestExposure = Instant.now()
        )
    }

    @Test
    fun `Latest exposure 1 days after with weekend adjusting`() {
        testDateFlagsReplacement(
            exposureType = LATEST_EXPOSURE,
            daysOffset = 1L,
            oldestExposure = Instant.now(),
            newestExposure = friday,
            adjustWeekend = true,
            weekendAdjuster = -1
        )
    }

    @Test
    fun `Latest exposure 2 days after with weekend adjusting`() {
        testDateFlagsReplacement(
            exposureType = LATEST_EXPOSURE,
            daysOffset = 2L,
            oldestExposure = Instant.now(),
            newestExposure = friday,
            adjustWeekend = true,
            weekendAdjuster = 1
        )
    }

    @Test
    fun `Latest exposure 14 days after without weekend adjusting`() {
        testDateFlagsReplacement(
            exposureType = LATEST_EXPOSURE,
            daysOffset = 14L,
            oldestExposure = Instant.now().minus(10, ChronoUnit.DAYS),
            newestExposure = Instant.now()
        )
    }

    @Test
    fun `Latest exposure 14 days after with saturday adjusting`() {
        testDateFlagsReplacement(
            exposureType = LATEST_EXPOSURE,
            daysOffset = 14L,
            oldestExposure = Instant.now(),
            newestExposure = saturday,
            adjustWeekend = true,
            weekendAdjuster = -1
        )
    }

    @Test
    fun `Latest exposure 14 days after with sunday adjusting`() {
        testDateFlagsReplacement(
            exposureType = LATEST_EXPOSURE,
            daysOffset = 14L,
            oldestExposure = Instant.now(),
            newestExposure = sunday,
            adjustWeekend = true,
            weekendAdjuster = 1
        )
    }

    @Test
    fun `Earliest exposure 1 days after with weekend adjusting`() {
        testDateFlagsReplacement(
            exposureType = EARLIEST_EXPOSURE,
            daysOffset = 1L,
            oldestExposure = friday,
            newestExposure = Instant.now(),
            adjustWeekend = true,
            weekendAdjuster = -1
        )
    }

    @Test
    fun `Earliest exposure 2 days after with weekend adjusting`() {
        testDateFlagsReplacement(
            exposureType = EARLIEST_EXPOSURE,
            daysOffset = 2L,
            oldestExposure = friday,
            newestExposure = Instant.now(),
            adjustWeekend = true,
            weekendAdjuster = 1
        )
    }

    @Test
    fun `Earliest exposure 5 days after without weekend adjusting`() {
        testDateFlagsReplacement(
            exposureType = EARLIEST_EXPOSURE,
            daysOffset = 5L,
            oldestExposure = Instant.now().minus(5, ChronoUnit.DAYS),
            newestExposure = Instant.now()
        )
    }

    @Test
    fun `Earliest exposure 14 days after without weekend adjusting`() {
        testDateFlagsReplacement(
            exposureType = EARLIEST_EXPOSURE,
            daysOffset = 14L,
            oldestExposure = Instant.now().minus(10, ChronoUnit.DAYS),
            newestExposure = Instant.now()
        )
    }

    @Test
    fun `Earliest exposure 14 days after with saturday adjusting`() {
        testDateFlagsReplacement(
            exposureType = EARLIEST_EXPOSURE,
            daysOffset = 14L,
            oldestExposure = saturday,
            newestExposure = Instant.now(),
            adjustWeekend = true,
            weekendAdjuster = -1
        )
    }

    @Test
    fun `Earliest exposure 14 days after with sunday adjusting`() {
        testDateFlagsReplacement(
            exposureType = EARLIEST_EXPOSURE,
            daysOffset = 14L,
            oldestExposure = sunday,
            newestExposure = Instant.now(),
            adjustWeekend = true,
            weekendAdjuster = 1
        )
    }

    private fun testDateFlagsReplacement(
        exposureType: String,
        daysOffset: Long,
        oldestExposure: Instant,
        newestExposure: Instant,
        adjustWeekend: Boolean = false,
        weekendAdjuster: Long = 0L
    ) {
        //given
        val adjust = if (adjustWeekend) "TRUE" else "FALSE"
        viewModel = HomeViewModel(
            enManager,
            userFlowRepository,
            preferences,
            object : FakeRiskLevelRepository() {
                override val riskLevelNextSteps = flowOf(
                    listOf(
                        NextStep(
                            type = NextStepType.WEBSITE,
                            description = "Stay at home until DAYS_FROM_EXPOSURE{$exposureType,$daysOffset,$adjust}."
                        )
                    )
                )
            })

        every { preferences.riskMetrics } returns RiskMetrics(
            0.0,
            leastRecentSignificantExposureDate = oldestExposure,
            mostRecentSignificantExposureDate = newestExposure
        )

        //when
        val nextSteps = viewModel.nextSteps.getOrAwaitValue()

        //then
        val dateToOffset = if (exposureType == LATEST_EXPOSURE) newestExposure else oldestExposure
        val requestedDate = dateToOffset.toLocalDate().plusDays(daysOffset)
        val adjustedDate =
            if (adjustWeekend) requestedDate.plusDays(weekendAdjuster) else requestedDate
        assertEquals(
            "Stay at home until ${DateFormatter.format(adjustedDate)}.",
            nextSteps.first().description
        )
    }

    companion object {
        const val EARLIEST_EXPOSURE = "EARLIEST"
        const val LATEST_EXPOSURE = "LATEST"
    }
}