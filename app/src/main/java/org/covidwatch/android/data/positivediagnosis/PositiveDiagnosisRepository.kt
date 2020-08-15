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
import java.time.Instant

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

    fun diagnoses() = local.diagnoses()

    suspend fun diagnosisKeys() = withContext(dispatchers.io) {
        val regions = countryCodeRepository.exposureRelevantCountryCodes()
        val urls = uriManager.downloadUrls(regions)
        val dir = randomDirName()

        // We convert to file id in oder to be safe from change in the endpoint
        val localKeys = keyFileRepository.providedKeys()

        urls.map { keyFileBatch ->
            val localBatchKeys = localKeys
                .filter { it.batch == keyFileBatch.batch }
                .map { it.url.fileId }

            // Convert server urls to ids
            val serverKeys = keyFileBatch.urls.map { it.fileId }

            val commonKeys = serverKeys.intersect(localBatchKeys).toList()

            // Keep only ids that intersect with local ids
            val keysToKeep = serverKeys.subtract(commonKeys).toList()

            // Remove ids that are not present in the ids to keep
            val keysToDelete = localBatchKeys.subtract(commonKeys).toList()
            keyFileRepository.remove(keysToDelete)

            // Convert keys to keep to urls
            val keysToDownload = keyFileBatch.urls.filter { keysToKeep.contains(it.fileId) }

            val files = keysToDownload.mapNotNull { remote.diagnosisKey(dir, it) }

            keyFileBatch.copy(keys = files, urls = keysToDownload)
        }
    }

    fun uploadDiagnosisKeys(uploadUrl: String, positiveDiagnosis: PositiveDiagnosis) =
        remote.uploadDiagnosisKeys(uploadUrl, positiveDiagnosis)

    private fun randomDirName(): String {
        val bytes = ByteArray(8)
        random.nextBytes(bytes)
        return encoding.encode(bytes)
    }

    suspend fun diagnosisByVerificationCode(code: String) = withContext(dispatchers.io) {
        local.diagnosisByVerificationCode(code)
    }

    suspend fun addPositiveDiagnosisReport(positiveDiagnosisItem: PositiveDiagnosisReport) =
        withContext(dispatchers.io) {
            local.addPositiveDiagnosisReport(positiveDiagnosisItem)
        }

    suspend fun updatePositiveDiagnosisReport(positiveDiagnosisItem: PositiveDiagnosisReport) =
        withContext(dispatchers.io) {
            local.updatePositiveDiagnosisReport(positiveDiagnosisItem)
        }

    suspend fun delete(diagnosis: PositiveDiagnosisReport) = withContext(dispatchers.io) {
        local.delete(diagnosis)
    }

    suspend fun deleteCachedForUpload() = withContext(dispatchers.io) {
        local.deleteCachedForUpload()
    }

    suspend fun deleteOlderThan(date: Instant) =
        withContext(dispatchers.io) { local.deleteOlderThan(date.toEpochMilli()) }

}