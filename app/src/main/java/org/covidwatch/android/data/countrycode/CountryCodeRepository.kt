package org.covidwatch.android.data.countrycode

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.withContext
import org.covidwatch.android.data.CountryCode
import org.covidwatch.android.domain.AppCoroutineDispatchers
import org.covidwatch.android.extension.io

class CountryCodeRepository(
    private val local: CountryCodeDao,
    private val dispatchers: AppCoroutineDispatchers
) {

    //TODO: Remove when we have actual logic for tracking current and visited countries
    init {
        GlobalScope.io {
            if (local.countryCodes().isEmpty()) {
                local.insert(CountryCode("US"))
            }
        }
    }

    suspend fun exposureRelevantCountryCodes() = withContext(dispatchers.io) {
        local.countryCodes().map { it.code }
    }
}