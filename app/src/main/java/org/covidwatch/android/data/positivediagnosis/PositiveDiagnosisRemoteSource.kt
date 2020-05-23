package org.covidwatch.android.data.positivediagnosis

import androidx.annotation.WorkerThread
import com.google.common.io.BaseEncoding
import com.google.gson.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okio.IOException
import org.covidwatch.android.data.PositiveDiagnosis
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.Type


class PositiveDiagnosisRemoteSource(private val httpClient: OkHttpClient) {

    private val jsonType = "application/json; charset=utf-8".toMediaType()
    private val gson: Gson = GsonBuilder().registerTypeHierarchyAdapter(
        ByteArray::class.java,
        ByteArrayToBase64TypeAdapter()
    ).create()

    @WorkerThread
    fun diagnosisKey(url: String): File? {
        val request = Request.Builder().url(url).build()

        return httpClient.newCall(request).execute().let { response ->
            if (!response.isSuccessful) return@let null

            toFile(response.body)
        }
    }

    @WorkerThread
    private fun toFile(body: ResponseBody?): File? {
        body ?: return null
        return try {
            val file = File("")
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                var fileSizeDownloaded: Long = 0

                inputStream = body.byteStream()
                outputStream = FileOutputStream(file)
                while (true) {
                    val read: Int = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                }
                outputStream.flush()
                file
            } catch (e: IOException) {
                null
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: IOException) {
            null
        }
    }

    @WorkerThread
    fun uploadDiagnosisKeys(
        uploadUrl: String,
        diagnosis: PositiveDiagnosis
    ) {
        val body: RequestBody = gson.toJson(diagnosis).toRequestBody(jsonType)
        val request: Request = Request.Builder()
            .url(uploadUrl)
            .post(body)
            .build()
        httpClient.newCall(request).execute()
    }

    private class ByteArrayToBase64TypeAdapter :
        JsonSerializer<ByteArray?>,
        JsonDeserializer<ByteArray?> {

        private val base64 = BaseEncoding.base64()

        @Throws(JsonParseException::class)
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): ByteArray {
            return base64.decode(json.asString)
        }

        override fun serialize(
            src: ByteArray?,
            typeOfSrc: Type?,
            context: JsonSerializationContext?
        ): JsonElement {
            return JsonPrimitive(base64.encode(src))
        }
    }
}