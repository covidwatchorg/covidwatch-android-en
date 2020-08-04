package org.covidwatch.android.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.covidwatch.android.data.converter.InstantConverter
import org.covidwatch.android.data.countrycode.CountryCodeDao
import org.covidwatch.android.data.diagnosiskeystoken.DiagnosisKeysToken
import org.covidwatch.android.data.diagnosiskeystoken.DiagnosisKeysTokenDao
import org.covidwatch.android.data.exposureinformation.ExposureInformationDao
import org.covidwatch.android.data.keyfile.KeyFile
import org.covidwatch.android.data.keyfile.KeyFileDao
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisReportDao

@Database(
    entities = [
        CovidExposureInformation::class,
        DiagnosisKeysToken::class,
        PositiveDiagnosisReport::class,
        CountryCode::class,
        KeyFile::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(InstantConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exposureInformationDao(): ExposureInformationDao
    abstract fun diagnosisKeysTokenDao(): DiagnosisKeysTokenDao
    abstract fun keyFileDao(): KeyFileDao
    abstract fun positiveDiagnosisReportDao(): PositiveDiagnosisReportDao
    abstract fun countryCodeDao(): CountryCodeDao
}