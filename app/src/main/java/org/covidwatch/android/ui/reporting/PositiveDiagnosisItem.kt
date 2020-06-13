package org.covidwatch.android.ui.reporting

import java.util.*

data class PositiveDiagnosisItem(
    val testStatus: TestStatus,
    val testDate: Date
)

enum class TestStatus {
    Verified,
    NeedsVerification
}