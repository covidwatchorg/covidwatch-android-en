package org.covidwatch.android.data.diagnosiskeystoken

import kotlinx.coroutines.withContext
import org.covidwatch.android.domain.AppCoroutineDispatchers

class DiagnosisKeysTokenRepository(
    private val local: DiagnosisKeysTokenLocalSource,
    private val dispatchers: AppCoroutineDispatchers
) {
    suspend fun exposedTokens() =
        withContext(dispatchers.io) { local.exposedTokens() }

    suspend fun insert(token: DiagnosisKeysToken) =
        withContext(dispatchers.io) { local.insert(token) }

    suspend fun delete(token: DiagnosisKeysToken) =
        withContext(dispatchers.io) { local.delete(token) }

    suspend fun delete(tokens: List<DiagnosisKeysToken>) =
        withContext(dispatchers.io) { local.delete(tokens) }

    suspend fun setExposed(token: String) = withContext(dispatchers.io) {
        val exposedToken = local.findByToken(token).copy(potentialExposure = true)
        local.update(exposedToken)
    }

    suspend fun delete(token: String) =
        withContext(dispatchers.io) { local.deleteByToken(token) }
}