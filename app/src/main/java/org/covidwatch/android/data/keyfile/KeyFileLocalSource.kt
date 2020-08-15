package org.covidwatch.android.data.keyfile

class KeyFileLocalSource(private val dao: KeyFileDao) {

    suspend fun add(keyFile: KeyFile) = dao.insert(keyFile)
    suspend fun keyFiles() = dao.keyFiles()
    suspend fun reset() = dao.reset()
    suspend fun remove(ids: List<String>) = dao.remove(ids)
}