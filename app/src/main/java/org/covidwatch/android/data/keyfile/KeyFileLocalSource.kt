package org.covidwatch.android.data.keyfile

class KeyFileLocalSource(private val dao: KeyFileDao) {

    suspend fun add(keyFile: KeyFile) = dao.insert(keyFile)

    suspend fun keyFiles() = dao.keyFiles()
}