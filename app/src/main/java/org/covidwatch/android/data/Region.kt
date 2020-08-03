package org.covidwatch.android.data

import com.google.gson.annotations.SerializedName
import org.covidwatch.android.data.NextStepType.*
import org.covidwatch.android.data.RegionId.*

data class Region(
    val id: RegionId?,
    val name: String,

    val nextStepsNoSignificantExposure: List<NextStep>,
    val nextStepsSignificantExposure: List<NextStep>,

    val nextStepsVerifiedPositive: List<NextStep>,
    val nextStepsVerificationCode: List<NextStep>,
    val exposureConfiguration: ExposureConfiguration = ExposureConfiguration(),

    @SerializedName("azRiskModelConfiguration")
    val riskModelConfiguration: RiskModelConfiguration = ArizonaRiskModelConfiguration(),

    /**
     * How many days since the last significant exposure considered to be a high risk exposure
     */
    val recentExposureDays: Int
)

class ExposureConfiguration(
    val minimumRiskScore: Int = 1,
    val attenuationDurationThresholds: IntArray = intArrayOf(50, 70),
    val attenuationLevelValues: IntArray = intArrayOf(1, 1, 1, 1, 1, 1, 1, 1),
    val daysSinceLastExposureLevelValues: IntArray = intArrayOf(1, 1, 1, 1, 1, 1, 1, 1),
    val durationLevelValues: IntArray = intArrayOf(1, 1, 1, 1, 1, 1, 1, 1),
    val transmissionRiskLevelValues: IntArray = intArrayOf(1, 1, 1, 1, 1, 1, 1, 1)
)

fun ExposureConfiguration.asCovidExposureConfiguration() = CovidExposureConfiguration(
    if (minimumRiskScore == 0) 1 else minimumRiskScore,
    attenuationLevelValues,
    daysSinceLastExposureLevelValues,
    durationLevelValues,
    transmissionRiskLevelValues,
    attenuationDurationThresholds
)

data class NextStep(
    val type: NextStepType,
    val description: String,
    val url: String? = null
)

enum class RegionId {
    @SerializedName("0")
    ARIZONA_STATE,

    @SerializedName("1")
    UOA,

    @SerializedName("2")
    ASU,

    @SerializedName("3")
    NAU
}

enum class NextStepType {
    @SerializedName("0")
    INFO,

    @SerializedName("1")
    PHONE,

    @SerializedName("2")
    WEBSITE,

    @SerializedName("3")
    SHARE
}

data class Regions(val regions: List<Region>)

object DefaultRegions {

    private val shareTheApp = NextStep(
        type = SHARE,
        description = "Share the app to improve your exposure notification accuracy.",
        url = "https://covidwatch.org"
    )

    private val nextStepsVerificationCodeDefault = NextStep(
        type = PHONE,
        description = "For others in this region, please call Arizona Department of Health Services at (844) 542-8201 for assistance.",
        url = "tel:1-844-542-8201"
    )

    private val default = Region(
        id = ARIZONA_STATE,
        name = "The State of Arizona",
        nextStepsNoSignificantExposure = listOf(shareTheApp),
        nextStepsSignificantExposure = listOf(shareTheApp),
        nextStepsVerifiedPositive = listOf(shareTheApp),
        nextStepsVerificationCode = listOf(
            NextStep(
                type = PHONE,
                description = "Please call Arizona Department of Health Services at (844) 542-8201 for assistance.",
                url = "tel:1-844-542-8201"
            )
        ),
        recentExposureDays = 14
    )

    private val universityOfArizona = Region(
        id = UOA,
        name = "University of Arizona",
        nextStepsNoSignificantExposure = listOf(
            NextStep(
                type = WEBSITE,
                description = "Monitor yourself for COVID-19 symptoms.",
                url = "https://covid19.arizona.edu/prevention-health/covid-19-symptoms?utm_source=covid_watch_app&utm_medium=referral&utm_campaign=covid_watch_covid19_symptoms_no_exposure"
            ),
            NextStep(
                type = PHONE,
                description = "If you have COVID-19 symptoms, call Campus Health at (520) 621-9202.",
                url = "tel:1-520-621-9202"
            ),
            NextStep(
                type = WEBSITE,
                description = "Protect yourself and others.",
                url = "http://covid19.arizona.edu/prevention-health/protect-yourself-others?utm_source=covid_watch_app&utm_medium=referral&utm_campaign=covid_watch_protect_yourself"
            ),
            shareTheApp
        ),
        nextStepsSignificantExposure = listOf(
            NextStep(
                type = WEBSITE,
                description = "Stay at home until DAYS_FROM_EXPOSURE{LATEST,14,FALSE}.",
                url = "http://covid19.arizona.edu/self-quarantine?utm_source=covid_watch_app&utm_medium=referral&utm_campaign=covid_watch_self_quarantine"
            ),
            NextStep(
                type = PHONE,
                description = "Call Campus Health at (520) 621-9202 and schedule a COVID-19 test for DAYS_FROM_EXPOSURE{EARLIEST,7,TRUE}.",
                url = "tel:1-520-621-9202"
            ),
            NextStep(
                type = WEBSITE,
                description = "Monitor COVID-19 symptoms and get tested ASAP if symptoms appear.",
                url = "https://covid19.arizona.edu/prevention-health/covid-19-symptoms?utm_source=covid_watch_app&utm_medium=referral&utm_campaign=covid_watch_covid19_symptoms"
            ),
            NextStep(
                type = WEBSITE,
                description = "Register with University of Arizona's Contact Tracing team.",
                url = "https://covid19.arizona.edu/app-redcap?utm_source=covid_watch_app&utm_medium=referral&utm_campaign=covid_watch_contact_tracing"
            ),
            shareTheApp
        ),
        nextStepsVerifiedPositive = listOf(
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
        ),
        nextStepsVerificationCode = listOf(
            NextStep(
                type = PHONE,
                description = "If you are a student or staff at UArizona, please call Campus Health Services at 520-621-9202 to obtain one. If you were tested in a different region, have a copy of your results ready.",
                url = "tel:1-520-621-9202"
            )
        ),
        recentExposureDays = 14
    )

    private val arizonaStateUniversity = Region(
        id = ASU,
        name = "Arizona State University",
        nextStepsNoSignificantExposure = listOf(shareTheApp),
        nextStepsSignificantExposure = listOf(shareTheApp),
        nextStepsVerifiedPositive = listOf(shareTheApp),
        nextStepsVerificationCode = listOf(nextStepsVerificationCodeDefault),
        recentExposureDays = 14
    )

    private val northernArizonaUniversity = Region(
        id = NAU,
        name = "Northern Arizona University",
        nextStepsNoSignificantExposure = listOf(shareTheApp),
        nextStepsSignificantExposure = listOf(shareTheApp),
        nextStepsVerifiedPositive = listOf(shareTheApp),
        nextStepsVerificationCode = listOf(nextStepsVerificationCodeDefault),
        recentExposureDays = 14
    )

    val all = listOf(
        default,
        universityOfArizona,
        arizonaStateUniversity,
        northernArizonaUniversity
    )
}