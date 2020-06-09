package org.covidwatch.android.ui

import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT
import androidx.databinding.BindingAdapter
import org.covidwatch.android.R
import org.covidwatch.android.data.CovidExposureInformation
import org.covidwatch.android.data.CovidExposureSummary
import org.covidwatch.android.data.RiskScoreLevel
import org.covidwatch.android.data.level
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

@BindingAdapter("exposure")
fun TextView.setTextFromExposure(exposure: CovidExposureInformation?) {
    exposure?.let {
        when (it.totalRiskScore.level) {
            RiskScoreLevel.HIGH -> {
                setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_risk_high,
                    0,
                    0,
                    0
                )
                text = HtmlCompat.fromHtml(
                    context.getString(
                        R.string.high_risk_exposure,
                        DateFormatter.format(it.dateMillisSinceEpoch)
                    ),
                    FROM_HTML_MODE_COMPACT
                )
            }
            RiskScoreLevel.MEDIUM -> {
                setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_risk_med,
                    0,
                    0,
                    0
                )
                text = HtmlCompat.fromHtml(
                    context.getString(
                        R.string.med_risk_exposure,
                        DateFormatter.format(it.dateMillisSinceEpoch)
                    ),
                    FROM_HTML_MODE_COMPACT
                )
            }
            RiskScoreLevel.NONE,
            RiskScoreLevel.LOW -> {
                setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_risk_low,
                    0,
                    0,
                    0
                )
                text = HtmlCompat.fromHtml(
                    context.getString(
                        R.string.low_risk_exposure,
                        DateFormatter.format(it.dateMillisSinceEpoch)
                    ),
                    FROM_HTML_MODE_COMPACT
                )
            }
        }
    }
}

@BindingAdapter("date")
fun TextView.setTextFromTime(time: Long?) {
    time ?: return
    text = DateFormatter.format(time)
}

@BindingAdapter("last_exposure_time")
fun TextView.setTextFromLastExposureTime(time: Long?) {
    time ?: return
    text = HtmlCompat.fromHtml(
        context.getString(R.string.last_exposure_time, DateFormatter.formatDateAndTime(time)),
        FROM_HTML_MODE_COMPACT
    )
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