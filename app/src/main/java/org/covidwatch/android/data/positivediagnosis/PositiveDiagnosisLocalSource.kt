package org.covidwatch.android.data.positivediagnosis

import org.covidwatch.android.data.PositiveDiagnosisReport

class PositiveDiagnosisLocalSource(private val dao: PositiveDiagnosisReportDao) {

    fun diagnoses() = dao.diagnoses()

    suspend fun addPositiveDiagnosisReport(report: PositiveDiagnosisReport) =
        dao.insert(report)

    suspend fun updatePositiveDiagnosisReport(report: PositiveDiagnosisReport) =
        dao.update(report)

    suspend fun report(id: String) = dao.report(id)

    suspend fun diagnosisByVerificationCode(code: String) =
        dao.diagnosisByVerificationCode(code)

    suspend fun delete(diagnosis: PositiveDiagnosisReport) = dao.delete(diagnosis)

    suspend fun deleteCachedForUpload() = dao.deleteCachedForUpload()

    suspend fun deleteOlderThan(date: Long) = dao.deleteOlderThan(date)
}