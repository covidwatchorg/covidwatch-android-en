package org.covidwatch.android.data.positivediagnosis

import org.covidwatch.android.data.ApiService
import org.covidwatch.android.data.PositiveDiagnosis
import java.io.File
import java.util.*

class PositiveDiagnosisRemoteSource(private val apiService: ApiService) {
    suspend fun diagnosisKeys(since: Date): List<File> = apiService.diagnosisKeys(since)

    suspend fun uploadDiagnosisKeys(keys: PositiveDiagnosis) {
        apiService.uploadDiagnosisKeys(keys)
    }

    suspend fun isNumberValid(phaNumber: String) = apiService.isNumberValid(phaNumber)
}