package org.covidwatch.android.data.countrycode

import kotlinx.coroutines.GlobalScope
import org.covidwatch.android.data.CountryCode
import org.covidwatch.android.extension.io

class CountryCodeRepository(private val local: CountryCodeDao) {

    //TODO: Remove when we have actual logic for tracking current and visited countries
    init {
        GlobalScope.io {
            if (local.countryCodes().isEmpty()) {
                local.insert(CountryCode("US"))
            }
        }
    }

    suspend fun exposureRelevantCountryCodes() =
        local.countryCodes().map { it.code }
}