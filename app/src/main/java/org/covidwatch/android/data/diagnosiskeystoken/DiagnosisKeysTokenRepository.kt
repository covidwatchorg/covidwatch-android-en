package org.covidwatch.android.data.diagnosiskeystoken

class DiagnosisKeysTokenRepository(private val local: DiagnosisKeysTokenLocalSource) {
    suspend fun exposedTokens() = local.exposedTokens()

    suspend fun insert(token: DiagnosisKeysToken) = local.insert(token)

    suspend fun update(token: DiagnosisKeysToken) = local.update(token)

    suspend fun delete(token: DiagnosisKeysToken) = local.delete(token)

    suspend fun delete(tokens: List<DiagnosisKeysToken>) = local.delete(tokens)

    suspend fun setExposed(token: String) {
        val exposedToken = local.findByToken(token).copy(potentialExposure = true)
        update(exposedToken)
    }
}