package org.covidwatch.android.data

import androidx.room.Database
import androidx.room.RoomDatabase
import org.covidwatch.android.data.exposureinformation.ExposureInformationDao
import org.covidwatch.android.data.diagnosiskeystoken.DiagnosisKeysToken
import org.covidwatch.android.data.diagnosiskeystoken.DiagnosisKeysTokenDao

@Database(
    entities = [CovidExposureInformation::class, DiagnosisKeysToken::class],
    version = 0,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exposureInformationDao(): ExposureInformationDao
    abstract fun diagnosisKeysTokenDao(): DiagnosisKeysTokenDao
}