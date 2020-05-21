package org.covidwatch.android.data

import androidx.room.Database
import androidx.room.RoomDatabase
import org.covidwatch.android.data.countrycode.CountryCodeDao
import org.covidwatch.android.data.diagnosiskeystoken.DiagnosisKeysToken
import org.covidwatch.android.data.diagnosiskeystoken.DiagnosisKeysTokenDao
import org.covidwatch.android.data.exposureinformation.ExposureInformationDao

@Database(
    entities = [
        CovidExposureInformation::class,
        DiagnosisKeysToken::class,
        CountryCode::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exposureInformationDao(): ExposureInformationDao
    abstract fun diagnosisKeysTokenDao(): DiagnosisKeysTokenDao
    abstract fun countryCodeDao(): CountryCodeDao
}