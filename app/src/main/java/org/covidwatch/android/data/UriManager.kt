package org.covidwatch.android.data

import okhttp3.OkHttpClient
import okhttp3.Request
import org.covidwatch.android.data.positivediagnosis.KeyFileBatch
import org.covidwatch.android.exposurenotification.ServerException
import java.util.regex.Pattern

class UriManager(
    private val serverUploadEndpoint: String,
    private val serverDownloadEndpoint: String,
    private val httpClient: OkHttpClient
) {
    private val batchNumPattern = Pattern.compile("exposureKeyExport-[A-Z]{2}/([0-9]+)-([0-9]+)-[0-9]+.zip")

    //TODO: Check if we going to use separate urls for different regions
    fun uploadUris(regions: List<String>) = listOf(serverUploadEndpoint)

    fun downloadUrls(regions: List<String>) = regions.map { regionFiles(it) }.flatten()

    private fun regionFiles(region: String): List<KeyFileBatch> {
        val request = Request.Builder()
            .url(indexUrl(region))
            .build()
        val response = httpClient.newCall(request).execute()
        if (response.code != 200) throw ServerException()
        val indexFile = response.body?.string()

        val regionFiles = indexFile
            ?.split("\n")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()

        return regionFiles
            .filter {
                batchNumPattern.matcher(it).matches()
            }
            .groupBy { it.split("-").last().removeSuffix(".zip") }
            .map {
                KeyFileBatch(
                    region,
                    batch = it.key.toInt(),
                    urls = it.value.map { url -> batchUrl(url) })
            }
    }

    private fun indexUrl(region: String): String {
        return "$serverDownloadEndpoint/exposureKeyExport-$region/index.txt"
    }

    private fun batchUrl(file: String): String {
        return "$serverDownloadEndpoint/$file"
    }
}