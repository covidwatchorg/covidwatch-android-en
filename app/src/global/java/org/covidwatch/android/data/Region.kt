package org.covidwatch.android.data

import org.covidwatch.android.data.NextStepType.*
import org.covidwatch.android.data.RegionId.BERMUDA

object RegionId {
    const val BERMUDA = 0
}

object DefaultRegions {

    private val shareTheApp = NextStep(
        type = SHARE,
        description = "Share the app to improve your exposure notification accuracy.",
        url = "https://covidwatch.org"
    )

    private val nextStepsVerificationCodeDefault = NextStep(
        type = WEBSITE,
        description = "For those outside of Bermuda, please visit the Covid Watch website to learn more about the app and which regions it supports.",
        url = "https://www.covidwatch.org"
    )

    private val bermuda = Region(
        id = BERMUDA,
        name = "Government of Bermuda",
        nextStepsNoSignificantExposure = listOf(
            NextStep(
                type = WEBSITE,
                description = "Monitor COVID-19 symptoms.",
                url = "https://www.gov.bm/sites/default/files/COVID-19-Symptom-Self-Assessment-v2.pdf"
            ),
            NextStep(
                type = PHONE,
                description = "If you have COVID-19 symptoms, call the Government COVID-19 Hotline at 1-(441)-444-2498.",
                url = "tel:1-441-444-2498"
            ),
            NextStep(
                type = WEBSITE,
                description = "Learn how to protect yourself and others.",
                url = "https://www.gov.bm/coronavirus-wellbeing"
            ),
            shareTheApp
        ),
        nextStepsSignificantExposure = listOf(
            NextStep(
                type = WEBSITE,
                description = "Monitor COVID-19 symptoms and get tested ASAP if symptoms appear.",
                url = "https://www.gov.bm/sites/default/files/COVID-19-Symptom-Self-Assessment-v2.pdf"
            ),
            NextStep(
                type = PHONE,
                description = "Call the Government COVID-19 Hotline at 1-(441)-444-2498 or your healthcare provider for guidance.",
                url = "tel:1-441-444-2498"
            ),
            NextStep(
                type = WEBSITE,
                description = "If you have symptoms follow the self-quarantine guidelines.",
                url = "https://www.gov.bm/sites/default/files/11436%20-%20Coronavirus%202020_Precautions%20Poster_2_0.pdf"
            ),
            shareTheApp
        ),
        nextStepsVerifiedPositive = listOf(
            NextStep(
                type = PHONE,
                description = "Follow up with the Government COVID-19 Hotline at 1-(441)-444-2498 or your healthcare provider for more instructions.",
                url = "tel:1-441-444-2498"
            ),
            shareTheApp
        ),
        nextStepsVerificationCode = listOf(
            NextStep(
                type = PHONE,
                description = "For those located in Bermuda, please call the Government COVID-19 Hotline at 1-(441)-444-2498 to obtain one. If you were tested elsewhere, please have the documentation of your test result ready",
                url = "tel:1-441-444-2498"
            ),
            nextStepsVerificationCodeDefault
        ),
        recentExposureDays = 14
    )

    val all = listOf(bermuda)
}