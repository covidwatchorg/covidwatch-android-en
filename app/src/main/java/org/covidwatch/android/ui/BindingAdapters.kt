package org.covidwatch.android.ui

import android.view.View
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT
import androidx.databinding.BindingAdapter
import com.skydoves.expandablelayout.ExpandableLayout
import org.covidwatch.android.R
import org.covidwatch.android.data.CovidExposureInformation
import org.covidwatch.android.data.CovidExposureSummary
import org.covidwatch.android.data.RiskLevel
import org.covidwatch.android.data.RiskLevel.*
import org.covidwatch.android.databinding.ExposureInformationDetailsBinding
import org.covidwatch.android.ui.util.DateFormatter
import java.util.*

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
fun ExpandableLayout.bindFromExposure(exposure: CovidExposureInformation?) {
    parentLayoutResource = R.layout.item_exposure_parent
    secondLayoutResource = R.layout.exposure_information_details

    exposure?.let {
        parentLayout.setOnClickListener {
            if (isExpanded) collapse() else expand()
        }
        parentLayout.findViewById<TextView>(R.id.text).text = DateFormatter.format(it.date)

        val demoExposureDetails = ExposureInformationDetailsBinding.bind(secondLayout)
        demoExposureDetails.exposure = it
    }
}

@BindingAdapter("attenuation_durations")
fun TextView.setTextFromAttenuationDurations(attenuationDurations: List<Int>?) {
    text = attenuationDurations?.joinToString { if (it >= 30) "≥30m" else "${it}m" }
}

@BindingAdapter("attenuation_thresholds")
fun TextView.setTextFromAttenuationThresholds(thresholds: IntArray?) {
    text = thresholds?.joinToString()
}

@BindingAdapter("total_risk")
fun TextView.setTextFromTotalRisk(totalRiskScore: Int?) {
    totalRiskScore?.let {
        text = context.getString(R.string.exposure_information_transmission_risk_text, it)
    }
}

@BindingAdapter("date")
fun TextView.setTextFromTime(time: Date?) {
    time ?: return
    text = DateFormatter.format(time)
}

@BindingAdapter("exposure_details_date")
fun TextView.setExposureInfoDate(time: Date?) {
    time ?: return
    text = HtmlCompat.fromHtml(
        context.getString(
            R.string.exposure_date_and_info,
            DateFormatter.format(time)
        ), FROM_HTML_MODE_COMPACT
    )
}

@BindingAdapter("last_exposure_time")
fun TextView.setTextFromLastExposureTime(time: Date?) {
    time ?: return
    text = HtmlCompat.fromHtml(
        context.getString(R.string.last_exposure_time, DateFormatter.formatDateAndTime(time)),
        FROM_HTML_MODE_COMPACT
    )
}

@BindingAdapter("risk_level")
fun TextView.setRiskLevelText(riskLevel: RiskLevel) {
    setText(
        when (riskLevel) {
            VERIFIED_POSITIVE -> R.string.high_risk_title
            HIGH -> R.string.high_risk_title
            LOW -> R.string.low_risk_title
        }
    )
}

@BindingAdapter("background_risk_level")
fun View.setBackgroundFromRiskLevel(riskLevel: RiskLevel) {
    background = context.getDrawable(
        when (riskLevel) {
            VERIFIED_POSITIVE -> R.color.high_risk
            HIGH -> R.color.high_risk
            LOW -> R.color.unknown_risk
        }
    )
}

