package org.covidwatch.android.ui.reporting

data class PositiveDiagnosisItem(
    val testStatus: TestStatus,
    val testDate: Long
)

enum class TestStatus {
    Verified,
    NeedsVerification
}