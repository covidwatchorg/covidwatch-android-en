package org.covidwatch.android.data.diagnosisverification

import com.google.gson.annotations.SerializedName

data class VerifyCodeResponse(
    @SerializedName("testtype")
    val testType: String?,
    @SerializedName("testdate")
    val testDate: String?,
    val token: String?,
    val error: String?
)