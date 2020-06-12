package org.covidwatch.android.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.covidwatch.android.data.converter.AttenuationDurationsConverter
import org.covidwatch.android.data.countrycode.CountryCodeDao
import org.covidwatch.android.data.diagnosiskeystoken.DiagnosisKeysToken
import org.covidwatch.android.data.diagnosiskeystoken.DiagnosisKeysTokenDao
import org.covidwatch.android.data.exposureinformation.ExposureInformationDao
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisReportDao

@Database(
    entities = [
        CovidExposureInformation::class,
        DiagnosisKeysToken::class,
        PositiveDiagnosisReport::class,
        CountryCode::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exposureInformationDao(): ExposureInformationDao
    abstract fun diagnosisKeysTokenDao(): DiagnosisKeysTokenDao
    abstract fun positiveDiagnosisReportDao(): PositiveDiagnosisReportDao
    abstract fun countryCodeDao(): CountryCodeDao
}