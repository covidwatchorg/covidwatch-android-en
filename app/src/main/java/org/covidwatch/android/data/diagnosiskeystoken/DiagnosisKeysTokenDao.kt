package org.covidwatch.android.data.diagnosiskeystoken

import androidx.room.Dao
import androidx.room.Query
import org.covidwatch.android.data.BaseDao

@Dao
interface DiagnosisKeysTokenDao : BaseDao<DiagnosisKeysToken> {

    @Query("SELECT * FROM diagnosis_keys_token WHERE potentialExposure = 1")
    suspend fun exposedTokens(): List<DiagnosisKeysToken>

    @Query("SELECT * FROM diagnosis_keys_token WHERE token = :token")
    suspend fun findByToken(token: String): DiagnosisKeysToken
}