package org.covidwatch.android.ui

import android.view.View
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT
import androidx.databinding.BindingAdapter
import org.covidwatch.android.R
import org.covidwatch.android.data.CovidExposureInformation
import org.covidwatch.android.data.CovidExposureSummary
import org.covidwatch.android.data.RiskScoreLevel
import org.covidwatch.android.data.RiskScoreLevel.*
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
            HIGH -> {
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
            MEDIUM -> {
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
            NONE,
            LOW -> {
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

@BindingAdapter("risk_level")
fun TextView.setRiskLevelText(riskScoreLevel: RiskScoreLevel) {
    setText(
        when (riskScoreLevel) {
            HIGH -> R.string.high_risk_title
            MEDIUM -> R.string.med_risk_title
            NONE,
            LOW -> R.string.low_risk_title
        }
    )
}

@BindingAdapter("background_risk_level")
fun View.setBackgroundFromRiskLevel(riskScoreLevel: RiskScoreLevel) {
    background = context.getDrawable(
        when (riskScoreLevel) {
            HIGH -> R.color.high_risk
            MEDIUM -> R.color.med_risk
            NONE,
            LOW -> R.color.low_risk
        }
    )
}

