package org.covidwatch.android.ui

import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT
import androidx.core.text.toSpannable
import androidx.databinding.BindingAdapter
import org.covidwatch.android.R
import org.covidwatch.android.data.CovidExposureInformation
import org.covidwatch.android.data.Region
import org.covidwatch.android.data.RiskLevel
import org.covidwatch.android.data.RiskLevel.*
import org.covidwatch.android.extension.fromHtml
import org.covidwatch.android.ui.util.DateFormatter
import java.time.Instant

@BindingAdapter("region")
fun TextView.setRegion(region: Region) {
    val text = context.getString(R.string.current_region, region.name).fromHtml()
    setText(
        text.toSpannable().apply {
            setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        context,
                        R.color.colorPrimary
                    )
                ),
                text.count() - region.name.length, text.count(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        },
        TextView.BufferType.SPANNABLE
    )
}

@BindingAdapter("exposure_info_attenuation")
fun TextView.setExposureInfoAttenuation(exposure: CovidExposureInformation?) {
    val attenuationDurations =
        exposure?.attenuationDurations?.joinToString { if (it >= 30) "≥30m" else "${it}m" }

    text = context.getString(
        R.string.exposure_info_attenuation,
        attenuationDurations
    ).fromHtml()
}

@BindingAdapter("exposure_info_transmission_risk")
fun TextView.setExposureInfoRisk(exposure: CovidExposureInformation?) {
    val riskLevel = context.getString(
        R.string.exposure_information_transmission_risk_text,
        exposure?.transmissionRiskLevel
    )
    text = context.getString(
        R.string.exposure_info_transmission_risk,
        riskLevel
    ).fromHtml()
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
fun TextView.setTextFromTime(time: Instant?) {
    time ?: return
    text = DateFormatter.format(time)
}

@BindingAdapter("exposure_details_date")
fun TextView.setExposureInfoDate(time: Instant?) {
    time ?: return
    text = HtmlCompat.fromHtml(
        context.getString(
            R.string.exposure_date_and_info,
            DateFormatter.format(time)
        ), FROM_HTML_MODE_COMPACT
    )
}

@BindingAdapter("last_exposure_time")
fun TextView.setTextFromLastExposureTime(time: Instant?) {
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
            else -> R.string.low_risk_title
        }
    )
}

@BindingAdapter("background_risk_level")
fun View.setBackgroundFromRiskLevel(riskLevel: RiskLevel) {
    background = ContextCompat.getDrawable(
        context,
        when (riskLevel) {
            VERIFIED_POSITIVE -> R.color.high_risk
            HIGH -> R.color.high_risk
            LOW -> R.color.unknown_risk
        }
    )
}

