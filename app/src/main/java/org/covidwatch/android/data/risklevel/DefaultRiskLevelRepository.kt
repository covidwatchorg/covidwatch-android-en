package org.covidwatch.android.data.risklevel

import androidx.lifecycle.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import org.covidwatch.android.data.model.NextStep
import org.covidwatch.android.data.model.RiskLevel
import org.covidwatch.android.data.model.TestType
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
import org.covidwatch.android.data.pref.PreferenceStorage
import org.covidwatch.android.domain.AppCoroutineDispatchers
import org.covidwatch.android.extension.daysTo
import java.time.Instant

interface RiskLevelRepository {
    val riskLevel: Flow<RiskLevel>
    val riskLevelNextSteps: Flow<List<NextStep>>
}

class DefaultRiskLevelRepository(
    val prefs: PreferenceStorage,
    val positiveDiagnosisRepository: PositiveDiagnosisRepository,
    val dispatchers: AppCoroutineDispatchers
) : RiskLevelRepository {

    override val riskLevel = combine(
        prefs.observableRiskMetrics.asFlow(),
        positiveDiagnosisRepository.diagnoses().asFlow(),
        prefs.observableRegion.asFlow()
    ) { risk, diagnoses, region ->
        val recentExposureDate = risk?.mostRecentSignificantExposureDate
        when {
            diagnoses.any { it.verified && TestType.CONFIRMED == it.verificationData?.testType } ->
                RiskLevel.VERIFIED_POSITIVE

            recentExposureDate != null && recentExposureDate.daysTo(Instant.now()) <= region.recentExposureDays ->
                RiskLevel.HIGH

            else -> RiskLevel.LOW
        }
    }.flowOn(dispatchers.io)

    override val riskLevelNextSteps = combine(
        riskLevel,
        prefs.observableRegion.asFlow()
    ) { riskLevel, region ->
        when (riskLevel) {
            RiskLevel.VERIFIED_POSITIVE -> region.nextStepsVerifiedPositive
            RiskLevel.HIGH -> region.nextStepsSignificantExposure
            RiskLevel.LOW -> region.nextStepsNoSignificantExposure
        }
    }.flowOn(dispatchers.io)
}