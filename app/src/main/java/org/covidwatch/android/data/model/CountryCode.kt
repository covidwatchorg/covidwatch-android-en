package org.covidwatch.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "country_code")
data class CountryCode(@PrimaryKey val code: String)