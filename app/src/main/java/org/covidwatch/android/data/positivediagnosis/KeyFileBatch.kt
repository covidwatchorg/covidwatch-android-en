package org.covidwatch.android.data.positivediagnosis

import java.io.File

data class KeyFileBatch(
    val region: String,
    val batch: Int,
    val keys: List<File> = emptyList(),
    val urls: List<String> = emptyList()
)