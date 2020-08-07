package org.covidwatch.android.data.positivediagnosis

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import org.covidwatch.android.data.BaseDao
import org.covidwatch.android.data.PositiveDiagnosisReport

@Dao
interface PositiveDiagnosisReportDao : BaseDao<PositiveDiagnosisReport> {

    @Query("SELECT * FROM positive_diagnosis_report")
    fun diagnoses(): LiveData<List<PositiveDiagnosisReport>>

    @Query("SELECT * FROM positive_diagnosis_report WHERE id = :id")
    suspend fun report(id: String): PositiveDiagnosisReport?

    @Query("SELECT * FROM positive_diagnosis_report WHERE verificationTestCode = :code")
    suspend fun diagnosisByVerificationCode(code: String): PositiveDiagnosisReport?

    @Query("DELETE FROM positive_diagnosis_report WHERE verified = 0 AND verificationTestCode IS NOT ''")
    suspend fun deleteCachedForUpload()
}