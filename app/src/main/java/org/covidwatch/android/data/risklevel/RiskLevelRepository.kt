package org.covidwatch.android.data.risklevel

import androidx.lifecycle.asFlow
import kotlinx.coroutines.flow.combine
import org.covidwatch.android.data.RiskLevel
import org.covidwatch.android.data.diagnosisverification.TestType
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
import org.covidwatch.android.data.pref.PreferenceStorage
import org.covidwatch.android.domain.AppCoroutineDispatchers

class RiskLevelRepository(
    val preferences: PreferenceStorage,
    val positiveDiagnosisRepository: PositiveDiagnosisRepository,
    val dispatchers: AppCoroutineDispatchers
) {

    val riskLevel = combine(
        preferences.observableRiskLevelValue.asFlow(),
        positiveDiagnosisRepository.positiveDiagnosisReports().asFlow(),
        preferences.observableRegion.asFlow()
    ) { risk, diagnoses, region ->
        when {
            diagnoses.any {
                it.verified && TestType.CONFIRMED == it.verificationData?.testType
            } -> RiskLevel.VERIFIED_POSITIVE
            risk != null && risk > region.riskHighThreshold -> RiskLevel.HIGH
            else -> RiskLevel.LOW
        }
    }

    val riskLevelNextSteps = combine(
        riskLevel,
        preferences.observableRegion.asFlow()
    ) { riskLevel, region ->
        when (riskLevel) {
            RiskLevel.VERIFIED_POSITIVE -> region.nextStepsVerifiedPositive
            RiskLevel.HIGH -> region.nextStepsSignificantExposure
            RiskLevel.LOW -> region.nextStepsNoSignificantExposure
        }
    }
}