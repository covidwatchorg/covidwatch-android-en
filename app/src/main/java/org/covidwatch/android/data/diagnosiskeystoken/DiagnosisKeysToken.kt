package org.covidwatch.android.data.diagnosiskeystoken

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diagnosis_keys_token")
data class DiagnosisKeysToken(
    @PrimaryKey val token: String,
    val providedTime: Long = System.currentTimeMillis(),
    val potentialExposure: Boolean = false
)