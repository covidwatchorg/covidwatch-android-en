package org.covidwatch.android.data.diagnosiskeystoken

class DiagnosisKeysTokenLocalSource(private val keysTokenDao: DiagnosisKeysTokenDao) {
    suspend fun exposedTokens() = keysTokenDao.exposedTokens()

    suspend fun insert(token: DiagnosisKeysToken) = keysTokenDao.insert(token)

    suspend fun update(token: DiagnosisKeysToken) = keysTokenDao.update(token)

    suspend fun delete(token: DiagnosisKeysToken) = keysTokenDao.delete(token)

    suspend fun delete(tokens: List<DiagnosisKeysToken>) =
        keysTokenDao.delete(tokens)

    suspend fun findByToken(token: String) = keysTokenDao.findByToken(token)
}