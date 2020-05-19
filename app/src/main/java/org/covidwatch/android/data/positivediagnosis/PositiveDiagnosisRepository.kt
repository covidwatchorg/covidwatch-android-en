package org.covidwatch.android.data.positivediagnosis

import org.covidwatch.android.data.PositiveDiagnosis
import java.io.File

class PositiveDiagnosisRepository(
    private val remote: PositiveDiagnosisRemoteSource,
    private val local: PositiveDiagnosisLocalSource
) {

    suspend fun diagnosisKeys(): List<File> {
        //TODO: Use a proper source for urls
        val urls = listOf<String>()

        val localKeys = local.diagnosisKeys()
        val remoteKeys = urls.mapNotNull {
            val diagnosisKey = remote.diagnosisKey(it)
            diagnosisKey
        }
        remoteKeys.forEach {
            local.saveKey(it)
        }
        return localKeys + remoteKeys
    }

    suspend fun isNumberValid(phaNumber: String) = remote.isNumberValid(phaNumber)

    suspend fun uploadDiagnosisKeys(positiveDiagnosis: PositiveDiagnosis) =
        remote.uploadDiagnosisKeys(positiveDiagnosis)
}