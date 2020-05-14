package org.covidwatch.android.ui.reporting

data class PositiveDiagnosisItem(
    val testStatus: TestStatus,
    val testDate: String
)

enum class TestStatus {
    Verified,
    NeedsVerification
}