package org.covidwatch.android.data

import java.io.File
import java.util.*

interface ApiService {
    suspend fun diagnosisKey(url: String): File
    suspend fun uploadDiagnosisKeys(keys: PositiveDiagnosis)
    suspend fun isNumberValid(phaNumber: String)
}