package org.covidwatch.android.data

import com.google.gson.annotations.SerializedName
import org.covidwatch.android.data.NextStepType.*
import org.covidwatch.android.data.RegionId.ARIZONA_STATE
import org.covidwatch.android.data.RegionId.ASU
import org.covidwatch.android.data.RegionId.NAU
import org.covidwatch.android.data.RegionId.UOA

data class Region(
    val id: Int,
    val name: String,
    val isDisabled: Boolean = false,

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
    val type: NextStepType?,
    val description: String,
    val url: String? = null
)

object RegionId {
    const val ARIZONA_STATE = 0
    const val UOA = 1
    const val ASU = 2
    const val NAU = 3
}

enum class NextStepType {
    @SerializedName("0")
    INFO,

    @SerializedName("1")
    PHONE,

    @SerializedName("2")
    WEBSITE,

    @SerializedName("3")
    SHARE,

    @SerializedName("4")
    SELECT_REGION
}

data class Regions(val regions: List<Region>)

object DefaultRegions {

    private val shareTheApp = NextStep(
        type = SHARE,
        description = "Share the app to improve your exposure notification accuracy.",
        url = "https://covidwatch.org"
    )

    private val nextStepsVerificationCodeDefault = NextStep(
        type = WEBSITE,
        description = "For others in Arizona, the statewide app is under development. Visit the Covid Watch website to let us know your thoughts on the app.",
        url = "https://www.covidwatch.org/partners/adhs-feedback"
    )

    private val stateOfArizona = Region(
        id = ARIZONA_STATE,
        name = "State of Arizona",
        isDisabled = true,
        nextStepsNoSignificantExposure = listOf(
            NextStep(
                type = WEBSITE,
                description = "Visit the Covid Watch website to share your feedback on the app.",
                url = "https://www.covidwatch.org/partners/adhs-feedback"
            ),
            shareTheApp
        ),
        nextStepsSignificantExposure = listOf(
            NextStep(
                type = WEBSITE,
                description = "Learn how to protect myself and others.",
                url = "https://azdhs.gov/preparedness/epidemiology-disease-control/infectious-disease-epidemiology/index.php#novel-coronavirus-what-everyone-needs"
            ),
            NextStep(
                type = WEBSITE,
                description = "Find a test site if symptoms appear.",
                url = "https://azdhs.gov/preparedness/epidemiology-disease-control/infectious-disease-epidemiology/index.php#novel-coronavirus-testing"
            ),
            shareTheApp
        ),
        nextStepsVerifiedPositive = listOf(
            NextStep(
                type = WEBSITE,
                description = "Learn how to protect myself and others.",
                url = "https://azdhs.gov/preparedness/epidemiology-disease-control/infectious-disease-epidemiology/index.php#novel-coronavirus-what-everyone-needs"
            ),
            NextStep(
                type = WEBSITE,
                description = "Find a test site if symptoms appear.",
                url = "https://azdhs.gov/preparedness/epidemiology-disease-control/infectious-disease-epidemiology/index.php#novel-coronavirus-testing"
            ),
            shareTheApp
        ),
        nextStepsVerificationCode = listOf(
            NextStep(
                type = SELECT_REGION,
                description = "Statewide app support is currently under development. You will continue to get exposure notifications, but can only share an anonymous COVID-19 diagnosis if you are part of a region with full app support."
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
                description = "Monitor COVID-19 symptoms.",
                url = "https://covid19.arizona.edu/prevention-health/covid-19-symptoms-prevention?utm_source=covid_watch_app&utm_medium=referral&utm_campaign=covid_watch_covid19_symptoms_no_exposure"
            ),
            NextStep(
                type = PHONE,
                description = "If you have COVID-19 symptoms, call Campus Health at (520) 621-9202.",
                url = "tel:1-520-621-9202"
            ),
            NextStep(
                type = WEBSITE,
                description = "Learn how to protect yourself and others.",
                url = "https://covid19.arizona.edu/face-coverings?utm_source=covid_watch_app&utm_medium=referral&utm_campaign=covid_watch_protect_yourself"
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
                url = "https://covid19.arizona.edu/prevention-health/covid-19-symptoms-prevention?utm_source=covid_watch_app&utm_medium=referral&utm_campaign=covid_watch_covid19_symptoms"
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
                description = "If you are a student, faculty, or staff member at University of Arizona, please call Campus Health Services at 520-621-9202 to obtain one. If you were tested elsewhere, please have your results ready.",
                url = "tel:1-520-621-9202"
            ),
            nextStepsVerificationCodeDefault
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
        nextStepsNoSignificantExposure = listOf(
            NextStep(
                type = WEBSITE,
                description = "Learn how to protect myself and others.",
                url = "https://in.nau.edu/campus-health-services/covid-19/"
            ),
            NextStep(
                type = WEBSITE,
                description = "Monitor COVID-19 symptoms.",
                url = "https://www.cdc.gov/coronavirus/2019-ncov/symptoms-testing/symptoms.html"
            ),
            NextStep(
                type = PHONE,
                description = "If you are a student, faculty, or staff at NAU, please call Campus Health Services at (928) 523-2131 to obtain one. If you were tested elsewhere, have a copy of your results ready.",
                url = "tel:1-928-523-2131"
            ),
            shareTheApp
        ),
        nextStepsSignificantExposure = listOf(
            NextStep(
                type = WEBSITE,
                description = "Monitor COVID-19 symptoms and get tested ASAP if symptoms appear.",
                url = "https://in.nau.edu/campus-health-services/covid-testing/"
            ),
            NextStep(
                type = WEBSITE,
                description = "If you have symptoms follow the self-quaratine guidelines.",
                url = "https://in.nau.edu/wp-content/uploads/sites/202/COVID-CHS-selfquarantine-7-16-20.pdf"
            ),
            NextStep(
                type = PHONE,
                description = "Call Campus Health at (928) 523-2131 or your health care provider for guidance.",
                url = "tel:1-928-523-2131"
            ),
            shareTheApp
        ),
        nextStepsVerifiedPositive = listOf(
            NextStep(
                type = WEBSITE,
                description = "Please stay at home and follow the self-isolation guidelines.",
                url = "https://in.nau.edu/wp-content/uploads/sites/202/COVID-CHS-selfisolation-7-16-201.pdf"
            ),
            NextStep(
                type = WEBSITE,
                description = "Register with NAU’s Exposure Tracing team.",
                url = "https://in.nau.edu/campus-health-services/exposure-tracing"
            ),
            NextStep(
                type = PHONE,
                description = "Follow up with Campus Health at (928) 523-2131 or your healthcare provider for more instructions.",
                url = "tel:1-928-523-2131"
            )
        ),
        nextStepsVerificationCode = listOf(
            NextStep(
                type = PHONE,
                description = "If you are a student, faculty, or staff at NAU, please call Campus Health Services at (928) 523-2131 to obtain one. If you were tested elsewhere, have a copy of your results ready.",
                url = "tel:1-928-523-2131"
            )
        ),
        recentExposureDays = 14
    )

    val all = listOf(
        stateOfArizona,
        northernArizonaUniversity,
        universityOfArizona
    )
}