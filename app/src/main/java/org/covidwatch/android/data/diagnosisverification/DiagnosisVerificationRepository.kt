package org.covidwatch.android.data.diagnosisverification

import kotlinx.coroutines.withContext
import org.covidwatch.android.domain.AppCoroutineDispatchers

class DiagnosisVerificationRepository(
    private val remote: DiagnosisVerificationRemoteSource,
    private val dispatchers: AppCoroutineDispatchers
) {

    suspend fun verify(testCode: String): String = withContext(dispatchers.io) {
        remote.verify(testCode)
    }

    suspend fun certificate(token: String, hmac: String): String = withContext(dispatchers.io) {
        remote.certificate(token, hmac)
    }
}