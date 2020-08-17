package org.covidwatch.android.data.positivediagnosis

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.covidwatch.android.data.UriManager
import org.covidwatch.android.data.countrycode.CountryCodeRepository
import org.covidwatch.android.data.keyfile.KeyFile
import org.covidwatch.android.data.keyfile.KeyFileRepository
import org.covidwatch.android.domain.AppCoroutineDispatchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class PositiveDiagnosisRepositoryTest {
    private val remote: PositiveDiagnosisRemoteSource = mockk()
    private val local: PositiveDiagnosisLocalSource = mockk()
    private val countryCodeRepository: CountryCodeRepository = mockk()
    private val uriManager: UriManager = mockk()
    private val keyFileRepository: KeyFileRepository = mockk()
    private val dispatchers: AppCoroutineDispatchers = AppCoroutineDispatchers()

    private lateinit var repository: PositiveDiagnosisRepository

    @BeforeEach
    fun init() {
        repository = PositiveDiagnosisRepository(
            remote,
            local,
            countryCodeRepository,
            uriManager,
            keyFileRepository,
            dispatchers
        )
    }

    @Test
    fun `Save all server keys`() = runBlocking {
        //given
        val serverKeys = listOf(
            KeyFileBatch(
                region = "",
                batch = 1,
                urls = listOf(
                    "dummy/1596389760-1596389820-00001.zip",
                    "dummy/1596389760-1596389830-00001.zip",
                    "dummy/1596389760-1596389840-00001.zip",
                    "dummy/1596389760-1596389850-00001.zip"
                )
            )
        )
        val localKeys = emptyList<KeyFile>()

        //when
        val keys = diagnosisKeys(serverKeys, localKeys)

        //then
        assertEquals(serverKeys, keys)
    }

    @Test
    fun `Save all server keys from different batches`() = runBlocking {
        //given
        val serverKeys = listOf(
            KeyFileBatch(
                region = "",
                batch = 1,
                urls = listOf(
                    "dummy/1596389760-1596389820-00001.zip",
                    "dummy/1596389760-1596389830-00001.zip",
                    "dummy/1596389760-1596389840-00001.zip",
                    "dummy/1596389760-1596389850-00001.zip"
                )
            ),
            KeyFileBatch(
                region = "",
                batch = 2,
                urls = listOf(
                    "dummy/1596389760-1596389820-00002.zip",
                    "dummy/1596389760-1596389830-00002.zip",
                    "dummy/1596389760-1596389840-00002.zip",
                    "dummy/1596389760-1596389850-00002.zip"
                )
            )
        )
        val localKeys = emptyList<KeyFile>()

        //when
        val keys = diagnosisKeys(serverKeys, localKeys)

        //then
        assertEquals(serverKeys, keys)
    }

    @Test
    fun `Save only new server keys`() = runBlocking {
        //given
        val serverKeys = listOf(
            KeyFileBatch(
                region = "",
                batch = 1,
                urls = listOf(
                    "dummy/1596389760-1596389820-00001.zip",
                    "dummy/1596389760-1596389830-00001.zip"
                )
            )
        )
        val localKeys = listOf(
            KeyFile(
                region = "",
                batch = 1,
                key = File(""),
                url = "dummy/1596389760-1596389820-00001.zip"
            )
        )
        //when
        val keys = diagnosisKeys(serverKeys, localKeys)

        //then
        assertEquals(
            listOf(
                KeyFileBatch(
                    region = "",
                    batch = 1,
                    urls = listOf(
                        "dummy/1596389760-1596389830-00001.zip"
                    )
                )
            ),
            keys
        )
    }

    @Test
    fun `Save only new server keys from different batches`() = runBlocking {
        //given
        val serverKeys = listOf(
            KeyFileBatch(
                region = "",
                batch = 1,
                urls = listOf(
                    "dummy/1596389760-1596389820-00001.zip",
                    "dummy/1596389760-1596389830-00001.zip"
                )
            ),
            KeyFileBatch(
                region = "",
                batch = 2,
                urls = listOf(
                    "dummy/1596389760-1596389820-00002.zip",
                    "dummy/1596389760-1596389830-00002.zip"
                )
            )
        )
        val localKeys = listOf(
            KeyFile(
                region = "",
                batch = 1,
                key = File(""),
                url = "dummy/1596389760-1596389820-00001.zip"
            ),
            KeyFile(
                region = "",
                batch = 2,
                key = File(""),
                url = "dummy/1596389760-1596389820-00002.zip"
            )
        )
        //when
        val keys = diagnosisKeys(serverKeys, localKeys)

        //then
        assertEquals(
            listOf(
                KeyFileBatch(
                    region = "",
                    batch = 1,
                    urls = listOf(
                        "dummy/1596389760-1596389830-00001.zip"
                    )
                ),
                KeyFileBatch(
                    region = "",
                    batch = 2,
                    urls = listOf(
                        "dummy/1596389760-1596389830-00002.zip"
                    )
                )
            ),
            keys
        )
    }

    @Test
    fun `Save only new server keys from from a new batch`() = runBlocking {
        //given
        val serverKeys = listOf(
            KeyFileBatch(
                region = "",
                batch = 1,
                urls = listOf(
                    "dummy/1596389760-1596389820-00001.zip",
                    "dummy/1596389760-1596389830-00001.zip"
                )
            ),
            KeyFileBatch(
                region = "",
                batch = 2,
                urls = listOf(
                    "dummy/1596389760-1596389820-00002.zip",
                    "dummy/1596389760-1596389830-00002.zip"
                )
            )
        )
        val localKeys = listOf(
            KeyFile(
                region = "",
                batch = 1,
                key = File(""),
                url = "dummy/1596389760-1596389820-00001.zip"
            )
        )
        //when
        val keys = diagnosisKeys(serverKeys, localKeys)

        //then
        assertEquals(
            listOf(
                KeyFileBatch(
                    region = "",
                    batch = 1,
                    urls = listOf(
                        "dummy/1596389760-1596389830-00001.zip"
                    )
                ),
                KeyFileBatch(
                    region = "",
                    batch = 2,
                    urls = listOf(
                        "dummy/1596389760-1596389820-00002.zip",
                        "dummy/1596389760-1596389830-00002.zip"
                    )
                )
            ),
            keys
        )
    }

    @Test
    fun `Skip server keys`() = runBlocking {
        //given
        val serverKeys = listOf(
            KeyFileBatch(
                region = "",
                batch = 1,
                urls = listOf(
                    "dummy/1596389760-1596389820-00001.zip",
                    "dummy/1596389760-1596389830-00001.zip"
                )
            )
        )
        val localKeys = listOf(
            KeyFile(
                region = "",
                batch = 1,
                key = File(""),
                url = "dummy/1596389760-1596389820-00001.zip"
            ),
            KeyFile(
                region = "",
                batch = 1,
                key = File(""),
                url = "dummy/1596389760-1596389830-00001.zip"
            )
        )

        //when
        val keys = diagnosisKeys(serverKeys, localKeys)

        //then
        assertTrue { keys.first().urls.isEmpty() }
    }

    @Test
    fun `Delete obsolete local keys and add new server keys`() = runBlocking {
        val serverKeysBatches = listOf(
            KeyFileBatch(
                region = "",
                batch = 1,
                urls = listOf(
                    "dummy/1596389760-1596389830-00001.zip",
                    "dummy/1596389760-1596389840-00001.zip"
                )
            )
        )

        val localKeyFiles = listOf(
            KeyFile(
                region = "",
                batch = 1,
                key = File(""),
                url = "dummy/1596389760-1596389820-00001.zip"
            ),
            KeyFile(
                region = "",
                batch = 1,
                key = File(""),
                url = "dummy/1596389760-1596389830-00001.zip"
            )
        )
        val keys = diagnosisKeys(serverKeysBatches, localKeyFiles)
        assertEquals(
            listOf(
                KeyFileBatch(
                    region = "",
                    batch = 1,
                    urls = listOf(
                        "dummy/1596389760-1596389840-00001.zip"
                    )
                )
            ), keys
        )
        coVerify { keyFileRepository.remove(listOf("1596389760-1596389820-00001")) }
    }

    @Test
    fun `Delete obsolete local keys and add new server keys from different batches`() =
        runBlocking {
            //given
            val serverKeysBatches = listOf(
                KeyFileBatch(
                    region = "",
                    batch = 1,
                    urls = listOf(
                        "dummy/1596389760-1596389830-00001.zip",
                        "dummy/1596389760-1596389840-00001.zip"
                    )
                ),
                KeyFileBatch(
                    region = "",
                    batch = 2,
                    urls = listOf(
                        "dummy/1596389760-1596389830-00002.zip",
                        "dummy/1596389760-1596389840-00002.zip"
                    )
                )
            )

            val localKeyFiles = listOf(
                KeyFile(
                    region = "",
                    batch = 1,
                    key = File(""),
                    url = "dummy/1596389760-1596389820-00001.zip"
                ),
                KeyFile(
                    region = "",
                    batch = 1,
                    key = File(""),
                    url = "dummy/1596389760-1596389830-00001.zip"
                ),
                KeyFile(
                    region = "",
                    batch = 2,
                    key = File(""),
                    url = "dummy/1596389760-1596389820-00002.zip"
                ),
                KeyFile(
                    region = "",
                    batch = 2,
                    key = File(""),
                    url = "dummy/1596389760-1596389830-00002.zip"
                )
            )

            //when
            val keys = diagnosisKeys(serverKeysBatches, localKeyFiles)

            //then
            val expectedKeys = listOf(
                KeyFileBatch(
                    region = "",
                    batch = 1,
                    urls = listOf(
                        "dummy/1596389760-1596389840-00001.zip"
                    )
                ),
                KeyFileBatch(
                    region = "",
                    batch = 2,
                    urls = listOf(
                        "dummy/1596389760-1596389840-00002.zip"
                    )
                )
            )
            assertEquals(expectedKeys, keys)
            expectedKeys.map { it.urls }.flatten().forEach {
                coVerify { remote.diagnosisKey(allAny(), it) }
            }
            coVerify { keyFileRepository.remove(listOf("1596389760-1596389820-00001")) }
            coVerify { keyFileRepository.remove(listOf("1596389760-1596389820-00002")) }
        }

    @Test
    fun `Delete obsolete local keys from one batch and add new server keys from another batches`() =
        runBlocking {
            val serverKeysBatches = listOf(
                KeyFileBatch(
                    region = "",
                    batch = 1,
                    urls = listOf(
                        "dummy/1596389760-1596389830-00001.zip",
                        "dummy/1596389760-1596389840-00001.zip"
                    )
                ),
                KeyFileBatch(
                    region = "",
                    batch = 2,
                    urls = listOf(
                        "dummy/1596389760-1596389830-00002.zip",
                        "dummy/1596389760-1596389840-00002.zip"
                    )
                ),
                KeyFileBatch(
                    region = "",
                    batch = 3,
                    urls = listOf(
                        "dummy/1596389760-1596389830-00003.zip",
                        "dummy/1596389760-1596389840-00003.zip"
                    )
                )
            )

            val localKeyFiles = listOf(
                KeyFile(
                    region = "",
                    batch = 1,
                    key = File(""),
                    url = "dummy/1596389760-1596389820-00001.zip"
                ),
                KeyFile(
                    region = "",
                    batch = 1,
                    key = File(""),
                    url = "dummy/1596389760-1596389830-00001.zip"
                )
            )
            val keys = diagnosisKeys(serverKeysBatches, localKeyFiles)
            val expectedKeys = listOf(
                KeyFileBatch(
                    region = "",
                    batch = 1,
                    urls = listOf(
                        "dummy/1596389760-1596389840-00001.zip"
                    )
                ),
                KeyFileBatch(
                    region = "",
                    batch = 2,
                    urls = listOf(
                        "dummy/1596389760-1596389830-00002.zip",
                        "dummy/1596389760-1596389840-00002.zip"
                    )
                ),
                KeyFileBatch(
                    region = "",
                    batch = 3,
                    urls = listOf(
                        "dummy/1596389760-1596389830-00003.zip",
                        "dummy/1596389760-1596389840-00003.zip"
                    )
                )
            )
            assertEquals(
                expectedKeys, keys
            )
            expectedKeys.map { it.urls }.flatten().forEach {
                coVerify { remote.diagnosisKey(allAny(), it) }
            }
            coVerify { keyFileRepository.remove(listOf("1596389760-1596389820-00001")) }
        }

    private suspend fun diagnosisKeys(
        serverKeys: List<KeyFileBatch>,
        localKeys: List<KeyFile>
    ): List<KeyFileBatch> {
        // mocks to make the code runnable
        coEvery { countryCodeRepository.exposureRelevantCountryCodes() } returns emptyList()
        coEvery { keyFileRepository.remove(allAny()) } returns Unit
        coEvery { remote.diagnosisKey(allAny(), allAny()) } returns null

        // mocks to test behavior
        every { uriManager.downloadUrls(allAny()) } returns serverKeys
        coEvery { keyFileRepository.providedKeys() } returns localKeys

        return repository.diagnosisKeys()
    }
}