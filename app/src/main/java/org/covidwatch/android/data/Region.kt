package org.covidwatch.android.data

import com.google.gson.annotations.SerializedName
import org.covidwatch.android.data.NextStepType.*

data class Region(
    val id: Int,
    val name: String,
    val logoTypeImageName: String,
    val logoImageName: String,
    val riskLowThreshold: Float,
    val riskHighThreshold: Float,
    val nextStepsRiskUnknown: List<NextStep>,
    val nextStepsRiskLow: List<NextStep>,
    val nextStepsRiskMedium: List<NextStep>,
    val nextStepsRiskHigh: List<NextStep>,
    val nextStepsRiskVerifiedPositive: List<NextStep>
)

data class NextStep(
    val type: NextStepType,
    val description: String,
    val url: String?
)

enum class NextStepType {
    @SerializedName("0")
    INFO,

    @SerializedName("1")
    PHONE,

    @SerializedName("2")
    WEBSITE,

    @SerializedName("3")
    GET_TESTED_DATES,

    @SerializedName("4")
    SHARE
}

data class Regions(val regions: List<Region>)

object DefaultRegions {
    private val infoAppIsActive = NextStep(
        type = INFO,
        description = "The app is active. You can now receive exposure notifications from others you were near who later report themselves as positive for COVID-19.",
        url = null
    )

    private val infoKeepAppInstalled = NextStep(
        type = INFO,
        description = "Keep the app installed until the pandemic is over so that you can continue to help reduce the spread in your communities.",
        url = null
    )

    private val shareTheApp = NextStep(
        type = SHARE,
        description = "Share the app to improve your exposure notification and risk level accuracy. It works best when everyone uses it.",
        url = "https://covidwatch.org"
    )

    val default = Region(
        id = 0,
        name = "Arizona State",
        logoTypeImageName = "Public Health Authority Logotype - Arizona State",
        logoImageName = "Public Health Authority Logo - Arizona State",
        riskLowThreshold = 0.14F,
        riskHighThreshold = 3.00F,
        nextStepsRiskUnknown = listOf(
            infoAppIsActive,
            infoKeepAppInstalled,
            NextStep(
                type = WEBSITE,
                description = "Visit the Public Health Website for local resources that are available to you.",
                url = "https://www.azdhs.gov"
            ),
            shareTheApp
        ),
        nextStepsRiskLow = listOf(
            shareTheApp
        ),
        nextStepsRiskMedium = listOf(
            shareTheApp
        ),
        nextStepsRiskHigh = listOf(
            shareTheApp
        ),
        nextStepsRiskVerifiedPositive = listOf(
            shareTheApp
        )
    )

    val universityOfArizona = Region(
        id = 1,
        name = "University of Arizona",
        logoTypeImageName = "Public Health Authority Logotype - University of Arizona",
        logoImageName = "Public Health Authority Logo - University of Arizona",
        riskLowThreshold = 0.14F,
        riskHighThreshold = 3.00F,
        nextStepsRiskUnknown = listOf(
            infoAppIsActive,
            infoKeepAppInstalled,
            NextStep(
                type = WEBSITE,
                description = "Visit the University of Arizona website for local resources that are available to you.",
                url = "https://arizona.edu"
            ),
            shareTheApp
        ),
        nextStepsRiskLow = listOf(
            NextStep(
                type = WEBSITE,
                description = "Monitor yourself for COVID-19 symtoms.",
                url = "http://covid19.arizona.edu/prevention-health/protect-yourself-others?utm_source=covid_watch_ios_app&utm_medium=referral&utm_campaign=covid_symptoms"
            ),
            NextStep(
                type = PHONE,
                description = "If you have COVID-19 symptoms, call Campus Health at (520) 621-9202.",
                url = "tel:1-520-621-9202"
            ),
            NextStep(
                type = WEBSITE,
                description = "Protect yourself and others.",
                url = "http://covid19.arizona.edu/prevention-health/protect-yourself-others?utm_source=covid_watch_ios_app&utm_medium=referral&utm_campaign=covid_watch_protect_yourself"
            ),
            shareTheApp
        ),
        nextStepsRiskMedium = listOf(
            NextStep(
                type = PHONE,
                description = "Call Campus Health at (520) 621-9202.",
                url = "tel:1-520-621-9202"
            ),
            NextStep(
                type = WEBSITE,
                description = "Monitor yourself for COVID-19 symtoms.",
                url = "http://covid19.arizona.edu/prevention-health/protect-yourself-others?utm_source=covid_watch_ios_app&utm_medium=referral&utm_campaign=covid_symptoms"
            ),
            shareTheApp
        ),
        nextStepsRiskHigh = listOf(
            NextStep(
                type = PHONE,
                description = "Stay at home and contact Campus Health at (520) 621-9202.",
                url = "tel:1-520-621-9202"
            ),
            NextStep(
                type = GET_TESTED_DATES,
                description = "Schedule a COVID-19 test between= ",
                url = null
            ),
            NextStep(
                type = WEBSITE,
                description = "Monitor COVID-19 symptoms and get tested ASAP if symptoms appear.",
                url = "http://covid19.arizona.edu/prevention-health/protect-yourself-others?utm_source=covid_watch_ios_app&utm_medium=referral&utm_campaign=covid_symptoms"
            ),
            NextStep(
                type = WEBSITE,
                description = "Register with University of Arizona's Contact Tracing team.",
                url = "https://covid19.arizona.edu/app-redcap?utm_source=covid_watch_app&utm_medium=referral&utm_campaign=covid_watch_contact_tracing"
            ),
            shareTheApp
        ),
        nextStepsRiskVerifiedPositive = listOf(
            NextStep(
                type = PHONE,
                description = "Follow up with Campus Health at (520) 621-9202 and your healthcare provider for more instructions.",
                url = "tel:1-520-621-9202"
            ),
            NextStep(
                type = WEBSITE,
                description = "Register with University of Arizona's Contact Tracing team.",
                url = "https://health.arizona.edu/SAFER?utm_source=covid_watch_app&utm_medium=referral&utm_campaign=covid_watch_case_management"
            ),
            shareTheApp
        )
    )

    val all = listOf(default, universityOfArizona)
}