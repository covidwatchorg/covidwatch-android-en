package org.covidwatch.android.data.keyfile

import kotlinx.coroutines.withContext
import org.covidwatch.android.domain.AppCoroutineDispatchers


class KeyFileRepository(
    private val local: KeyFileLocalSource,
    private val dispatchers: AppCoroutineDispatchers
) {
    suspend fun add(keyFile: KeyFile) = withContext(dispatchers.io) { local.add(keyFile) }

    suspend fun providedKeys(): List<KeyFile> = withContext(dispatchers.io) { local.keyFiles() }

    suspend fun reset() = withContext(dispatchers.io) { local.reset() }

    suspend fun remove(ids: List<String>) = withContext(dispatchers.io) { local.remove(ids) }
}