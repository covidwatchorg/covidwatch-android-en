package org.covidwatch.android.data

import java.io.File
import java.util.Date

class FirebaseService {
    suspend fun diagnosisKeys(since: Date): List<File> {
        TODO("not implemented")
    }

    suspend fun uploadDiagnosisKeys(keys: PositiveDiagnosis) {
        TODO("not implemented")
    }

    suspend fun isNumberValid(phaNumber: String) = true
}