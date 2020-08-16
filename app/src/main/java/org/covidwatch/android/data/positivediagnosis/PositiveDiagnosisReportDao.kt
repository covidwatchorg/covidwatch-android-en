package org.covidwatch.android.data.positivediagnosis

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import org.covidwatch.android.data.BaseDao
import org.covidwatch.android.data.PositiveDiagnosisReport

@Dao
interface PositiveDiagnosisReportDao : BaseDao<PositiveDiagnosisReport> {

    @Query("SELECT * FROM positive_diagnosis_report WHERE uploaded = 1")
    fun diagnoses(): LiveData<List<PositiveDiagnosisReport>>

    @Query("SELECT * FROM positive_diagnosis_report WHERE verificationTestCode = :code")
    suspend fun diagnosisByVerificationCode(code: String): PositiveDiagnosisReport?

    @Query("DELETE FROM positive_diagnosis_report WHERE verified = 0 AND verificationTestCode IS NOT ''")
    suspend fun deleteCachedForUpload()

    @Query("DELETE FROM positive_diagnosis_report WHERE reportDate < :date")
    suspend fun deleteOlderThan(date: Long)
}