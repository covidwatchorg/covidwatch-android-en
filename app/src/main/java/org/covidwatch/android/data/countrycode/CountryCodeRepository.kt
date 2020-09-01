package org.covidwatch.android.data.countrycode

interface CountryCodeRepository {
    suspend fun exposureRelevantCountryCodes(): List<String>
}