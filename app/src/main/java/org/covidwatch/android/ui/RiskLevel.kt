package org.covidwatch.android.ui

import android.content.Context
import org.covidwatch.android.R
import org.covidwatch.android.data.model.RiskLevel

fun RiskLevel.name(context: Context) = when (this) {
    RiskLevel.VERIFIED_POSITIVE -> context.getString(R.string.verified_positive_risk_name)
    RiskLevel.HIGH -> context.getString(R.string.high_risk_name)
    RiskLevel.LOW -> context.getString(R.string.no_exposures_risk_name)
}