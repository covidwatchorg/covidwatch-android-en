package org.covidwatch.android.data.positivediagnosis

import androidx.annotation.WorkerThread
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import okio.IOException
import org.covidwatch.android.data.PositiveDiagnosis
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class PositiveDiagnosisRemoteSource(private val httpClient: OkHttpClient) {

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

    suspend fun uploadDiagnosisKeys(keys: PositiveDiagnosis) {
        TODO()
    }

    suspend fun isNumberValid(phaNumber: String) = true
}