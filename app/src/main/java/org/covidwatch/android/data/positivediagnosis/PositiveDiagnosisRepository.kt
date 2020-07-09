package org.covidwatch.android.data.positivediagnosis

import com.google.common.io.BaseEncoding
import kotlinx.coroutines.withContext
import org.covidwatch.android.data.PositiveDiagnosis
import org.covidwatch.android.data.PositiveDiagnosisReport
import org.covidwatch.android.data.UriManager
import org.covidwatch.android.data.countrycode.CountryCodeRepository
import org.covidwatch.android.data.keyfile.KeyFileRepository
import org.covidwatch.android.domain.AppCoroutineDispatchers
import java.security.SecureRandom

class PositiveDiagnosisRepository(
    private val remote: PositiveDiagnosisRemoteSource,
    private val local: PositiveDiagnosisLocalSource,
    private val countryCodeRepository: CountryCodeRepository,
    private val uriManager: UriManager,
    private val keyFileRepository: KeyFileRepository,
    private val dispatchers: AppCoroutineDispatchers
) {
    private val random = SecureRandom()
    private val encoding = BaseEncoding.base32().lowerCase().omitPadding()

    fun positiveDiagnosisReports() = local.reports()

    suspend fun diagnosisKeys() = withContext(dispatchers.io) {
        val regions = countryCodeRepository.exposureRelevantCountryCodes()
        val urls = uriManager.downloadUrls(regions)
        val dir = randomDirName()
        val checkedFiles = keyFileRepository.providedKeys().map { it.url }

        urls.map { keyFileBatch ->
            val files = keyFileBatch.urls
                .filterNot { checkedFiles.contains(it) }
                .mapNotNull { remote.diagnosisKey(dir, it) }

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

    suspend fun addPositiveDiagnosisReport(positiveDiagnosisItem: PositiveDiagnosisReport) =
        withContext(dispatchers.io) {
            local.addPositiveDiagnosisReport(positiveDiagnosisItem)
        }

    suspend fun updatePositiveDiagnosisReport(positiveDiagnosisItem: PositiveDiagnosisReport) =
        withContext(dispatchers.io) {
            local.updatePositiveDiagnosisReport(positiveDiagnosisItem)
        }
}