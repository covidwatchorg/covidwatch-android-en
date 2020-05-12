package org.covidwatch.android.ui

import android.widget.TextView
import androidx.databinding.BindingAdapter
import org.covidwatch.android.R
import org.covidwatch.android.data.CovidExposureSummary
import org.covidwatch.android.ui.util.DateFormatter

@BindingAdapter("exposureSummary")
fun TextView.setExposureSummary(exposureSummary: CovidExposureSummary?) {
    exposureSummary?.let {
        text = context.getString(
            R.string.exposure_summary,
            it.daySinceLastExposure,
            it.matchedKeyCount,
            it.maximumRiskScore
        )
    }
    if (exposureSummary == null) {
        text = context.getString(R.string.no_exposure)
    }
}

@BindingAdapter("date")
fun TextView.setTextFromTime(time: Long?) {
    text = DateFormatter.format(time)
}

@BindingAdapter("total_risk")
fun TextView.setTextFromTotalRisk(totalRiskScore: Int?) {
    totalRiskScore?.let {
        //TODO: Use proper logic for mapping values
        text = when (it) {
            in 0..3 -> context.getString(R.string.low_total_exposure_risk, it)
            in 4..5 -> context.getString(R.string.middle_total_exposure_risk, it)
            else -> context.getString(R.string.high_total_exposure_risk, it)
        }
    }
}