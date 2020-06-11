package org.covidwatch.android.data

import com.google.android.gms.nearby.exposurenotification.ExposureInformation
import com.google.android.gms.nearby.exposurenotification.ExposureSummary

interface EnConverter {
    fun covidExposureSummary(exposureSummary: ExposureSummary): CovidExposureSummary
    fun covidExposureInformation(exposureInformation: ExposureInformation): CovidExposureInformation
}

@Suppress("unused")
class DefaultEnConverter : EnConverter {
    override fun covidExposureSummary(exposureSummary: ExposureSummary) =
        with(exposureSummary) {
            CovidExposureSummary(
                daysSinceLastExposure,
                matchedKeyCount,
                (maximumRiskScore * 8.0 / 4096).toInt(),
                attenuationDurationsInMinutes,
                (summationRiskScore * 8.0 / 4096).toInt()
            )
        }

    override fun covidExposureInformation(exposureInformation: ExposureInformation) =
        with(exposureInformation) {
            CovidExposureInformation(
                dateMillisSinceEpoch = dateMillisSinceEpoch,
                durationMinutes = durationMinutes,
                attenuationValue = attenuationValue,
                transmissionRiskLevel = transmissionRiskLevel,
                totalRiskScore = (totalRiskScore * 8.0 / 4096).toInt()
            )
        }
}