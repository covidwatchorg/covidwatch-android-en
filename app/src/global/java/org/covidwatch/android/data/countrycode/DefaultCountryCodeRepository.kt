package org.covidwatch.android.data.countrycode

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.withContext
import org.covidwatch.android.data.model.CountryCode
import org.covidwatch.android.domain.AppCoroutineDispatchers
import org.covidwatch.android.extension.io

class DefaultCountryCodeRepository(
    private val local: CountryCodeDao,
    private val dispatchers: AppCoroutineDispatchers
) : CountryCodeRepository {

    //TODO: Remove when we have actual logic for tracking current and visited countries
    init {
        GlobalScope.io {
            // TODO: 31.08.2020 A temp fix for testing compatibility between old testing build and new ones.
            //  REMOVE when before publishing to the store
            val usRegion = CountryCode("US")
            if (local.countryCodes().contains(usRegion)) {
                local.delete(usRegion)
            }

            if (local.countryCodes().isEmpty()) {
                local.insert(CountryCode("BM"))
            }
        }
    }

    override suspend fun exposureRelevantCountryCodes() = withContext(dispatchers.io) {
        local.countryCodes().map { it.code }
    }
}