package org.covidwatch.android.data.positivediagnosis

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.covidwatch.android.data.UriManager
import org.covidwatch.android.data.countrycode.CountryCodeRepository
import org.covidwatch.android.data.keyfile.KeyFile
import org.covidwatch.android.data.keyfile.KeyFileRepository
import org.covidwatch.android.domain.AppCoroutineDispatchers
import org.junit.jupiter.api.Test
import java.io.File

internal class PositiveDiagnosisRepositoryTest {
    private val remote: PositiveDiagnosisRemoteSource = mockk()
    private val local: PositiveDiagnosisLocalSource = mockk()
    private val countryCodeRepository: CountryCodeRepository = mockk()
    private val uriManager: UriManager = mockk()
    private val keyFileRepository: KeyFileRepository = mockk()
    private val dispatchers: AppCoroutineDispatchers = AppCoroutineDispatchers()

    private lateinit var repository: PositiveDiagnosisRepository

    @Test
    fun diagnosisKeys() = runBlocking {
        repository = PositiveDiagnosisRepository(
            remote,
            local,
            countryCodeRepository,
            uriManager,
            keyFileRepository,
            dispatchers
        )
        coEvery { countryCodeRepository.exposureRelevantCountryCodes() } returns emptyList()
        every { uriManager.downloadUrls(allAny()) } returns listOf(
            KeyFileBatch(
                region = "",
                batch = 1,
                urls = listOf("https://storage.googleapis.com/exposure-notification-export-fxega/exposureKeyExport-US/1596389760-1596389820-00001.zip")
            )
        )
        coEvery { keyFileRepository.remove(allAny()) } returns Unit
        coEvery { keyFileRepository.providedKeys() } returns listOf(
            KeyFile(
                region = "",
                batch = 1,
                key = File(""),
                url = "https://storage.googleapis.com/exposure-notification-export-fxega/exposureKeyExport-US/1596389760-1596389820-00001.zip"
            )
        )
        coEvery { remote.diagnosisKey(allAny(), allAny()) } returns File("")

        val keys = repository.diagnosisKeys()
    }
}