package org.covidwatch.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.android.gms.nearby.exposurenotification.*
import org.covidwatch.android.data.converter.ScanInstanceConverter
import java.time.Instant
import java.util.*

@Entity(tableName = "exposure_window")
@TypeConverters(ScanInstanceConverter::class)
data class CovidExposureWindow(
    val date: Instant,
    val scanInstances: List<CovidScanInstance>,
    @ReportType
    val reportType: Int,
    @Infectiousness
    val infectiousness: Int,
    @CalibrationConfidence
    val calibrationConfidence: Int,
    @PrimaryKey val id: String = UUID.randomUUID().toString()
)

fun ExposureWindow.asCovidExposureWindow() = CovidExposureWindow(
    Instant.ofEpochMilli(dateMillisSinceEpoch),
    scanInstances.map { it.asCovidScanInstance() },
    reportType,
    infectiousness,
    calibrationConfidence
)

private fun ScanInstance.asCovidScanInstance() = CovidScanInstance(
    typicalAttenuation = typicalAttenuationDb,
    minAttenuation = minAttenuationDb,
    secondsSinceLastScan = secondsSinceLastScan
)