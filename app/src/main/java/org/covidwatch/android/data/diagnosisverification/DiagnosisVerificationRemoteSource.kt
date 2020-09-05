package org.covidwatch.android.data.diagnosisverification

import androidx.annotation.WorkerThread
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.covidwatch.android.data.model.VerificationCertificateRequest
import org.covidwatch.android.data.model.VerificationCertificateResponse
import org.covidwatch.android.data.model.VerifyCodeRequest
import org.covidwatch.android.data.model.VerifyCodeResponse
import org.covidwatch.android.exposurenotification.ServerException

class DiagnosisVerificationRemoteSource(
    private val apiKey: String,
    private val verificationServerEndpoint: String,
    private val gson: Gson,
    private val httpClient: OkHttpClient
) {
    private val jsonType = "application/json; charset=utf-8".toMediaType()

    @WorkerThread
    fun verify(testCode: String): VerifyCodeResponse {
        val body: RequestBody = gson.toJson(VerifyCodeRequest(testCode)).toRequestBody(jsonType)

        val request = Request.Builder()
            .header("X-API-Key", apiKey)
            .post(body)
            .url("$verificationServerEndpoint/api/verify").build()

        return httpClient.newCall(request).execute().let { response ->
            val verifyCodeResponse = try {
                gson.fromJson(response.body?.charStream(), VerifyCodeResponse::class.java)
            } catch (e: Exception) {
                throw ServerException(response.body?.string())
            }

            if (verifyCodeResponse.token == null) throw ServerException(verifyCodeResponse.error)
            verifyCodeResponse
        }
    }

    fun certificate(token: String, hmac: String): String {
        val body: RequestBody =
            gson.toJson(VerificationCertificateRequest(token, hmac)).toRequestBody(jsonType)

        val request = Request.Builder()
            .header("X-API-Key", apiKey)
            .post(body)
            .url("$verificationServerEndpoint/api/certificate").build()

        return httpClient.newCall(request).execute().let { response ->
            val certificateResponse = try {
                gson.fromJson(
                    response.body?.charStream(),
                    VerificationCertificateResponse::class.java
                )
            } catch (e: Exception) {
                throw ServerException(response.body?.string())
            }
            certificateResponse.certificate ?: throw ServerException(certificateResponse.error)
        }
    }
}
