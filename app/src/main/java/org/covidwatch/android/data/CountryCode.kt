package org.covidwatch.android.data

import androidx.room.Entity

@Entity(tableName = "country_code")
data class CountryCode(val code: String)