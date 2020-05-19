package org.covidwatch.android.data.positivediagnosis

import java.io.File

class PositiveDiagnosisLocalSource() {
    suspend fun diagnosisKeys(): List<File> = listOf()
    suspend fun saveKey(file: File) = Unit
}