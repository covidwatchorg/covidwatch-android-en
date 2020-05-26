package org.covidwatch.android.data.positivediagnosis

import com.google.common.io.BaseEncoding
import org.covidwatch.android.data.PositiveDiagnosis
import org.covidwatch.android.data.PositiveDiagnosisReport
import org.covidwatch.android.data.UriManager
import org.covidwatch.android.data.countrycode.CountryCodeRepository
import java.security.SecureRandom

class PositiveDiagnosisRepository(
    private val remote: PositiveDiagnosisRemoteSource,
    private val local: PositiveDiagnosisLocalSource,
    private val countryCodeRepository: CountryCodeRepository,
    private val uriManager: UriManager
) {
    private val random = SecureRandom()
    private val encoding = BaseEncoding.base32().lowerCase().omitPadding()

    fun positiveDiagnosisReports() = local.reports()

    suspend fun diagnosisKeys(): List<KeyFileBatch> {
        val regions = countryCodeRepository.exposureRelevantCountryCodes()
        val urls = uriManager.downloadUrls(regions)

        val dir = randomDirName()
        return urls.map { keyFileBatch ->
            val files = keyFileBatch.urls.mapNotNull { remote.diagnosisKey(dir, it) }

            keyFileBatch.copy(keys = files)
        }
    }

    fun uploadDiagnosisKeys(uploadUrl: String, positiveDiagnosis: PositiveDiagnosis) =
        remote.uploadDiagnosisKeys(uploadUrl, positiveDiagnosis)

    private fun randomDirName(): String {
        val bytes = ByteArray(8)
        random.nextBytes(bytes)
        return encoding.encode(bytes)
    }

    suspend fun addPositiveDiagnosisReport(positiveDiagnosisItem: PositiveDiagnosisReport) {
        local.addPositiveDiagnosisReport(positiveDiagnosisItem)
    }
}