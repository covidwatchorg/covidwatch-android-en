package org.covidwatch.android.data.countrycode

import androidx.room.Dao
import androidx.room.Query
import org.covidwatch.android.data.BaseDao
import org.covidwatch.android.data.model.CountryCode

@Dao
interface CountryCodeDao : BaseDao<CountryCode> {
    @Query("SELECT * FROM country_code")
    suspend fun countryCodes(): List<CountryCode>
}