package org.covidwatch.android.data.diagnosisverification

import com.google.gson.annotations.SerializedName

data class VerificationCertificateRequest(
    val token: String,
    @SerializedName("ekeyhmac")
    val exposureKeyHmac: String
)