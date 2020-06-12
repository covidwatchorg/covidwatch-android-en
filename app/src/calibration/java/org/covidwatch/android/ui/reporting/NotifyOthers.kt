package org.covidwatch.android.ui.reporting

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import org.covidwatch.android.BuildConfig
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
import org.covidwatch.android.domain.ExportDiagnosisKeysAsFileUseCase
import org.covidwatch.android.domain.StartUploadDiagnosisKeysWorkUseCase
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.extension.launchUseCase
import org.covidwatch.android.extension.observeEvent
import org.covidwatch.android.extension.send
import org.covidwatch.android.ui.event.Event
import java.io.File
import java.net.URLConnection

class NotifyOthersFragment : BaseNotifyOthersFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewModel) {
            observeEvent(chooseShareMethod) {
                context?.let { context ->
                    val dialogView =
                        org.covidwatch.android.databinding.DialogChooseExportDiagnosisTypeBinding.inflate(
                            android.view.LayoutInflater.from(context)
                        )

                    val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(context)
                    dialog.setContentView(dialogView.root)
                    dialog.show()

                    dialogView.btnShareWithServer.setOnClickListener {
                        viewModel.shareReportAs(file = false)
                        dialog.dismiss()
                    }
                    dialogView.btnShareAsFile.setOnClickListener {
                        viewModel.shareReportAs(file = true)
                        dialog.dismiss()
                    }
                }
            }
            observeEvent(shareDiagnosisFile) { shareFile(it) }
        }
    }

    private fun shareFile(zipFiles: List<File>) {
        context?.let { context ->
            val zip = zipFiles.firstOrNull() ?: return
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            val uri = FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".fileprovider",
                zip
            )
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.type = URLConnection.guessContentTypeFromName(zip.name)
            startActivity(Intent.createChooser(shareIntent, null))
        }
    }
}

class NotifyOthersViewModel(
    private val exportDiagnosisKeysAsFileUseCase: ExportDiagnosisKeysAsFileUseCase,
    startUploadDiagnosisKeysWorkUseCase: StartUploadDiagnosisKeysWorkUseCase,
    enManager: ExposureNotificationManager,
    positiveDiagnosisRepository: PositiveDiagnosisRepository
) : BaseNotifyOthersViewModel(
    startUploadDiagnosisKeysWorkUseCase,
    enManager,
    positiveDiagnosisRepository
) {
    private val riskLevels = mutableListOf<Int>()

    private val _shareDiagnosisFile = MutableLiveData<Event<List<File>>>()
    val shareDiagnosisFile: LiveData<Event<List<File>>> = _shareDiagnosisFile

    private val _chooseShareMethod = MutableLiveData<Event<Unit>>()
    val chooseShareMethod: LiveData<Event<Unit>> = _chooseShareMethod

    override fun shareReport(riskLevels: String) {
        // TODO: 03.06.2020 Add validation that check correct format of the string
        this.riskLevels.clear()
        this.riskLevels.addAll(stringToRiskLevels(riskLevels))
        _chooseShareMethod.send()
    }

    fun shareReportAs(file: Boolean) {
        if (file) {
            viewModelScope.launchUseCase(
                exportDiagnosisKeysAsFileUseCase,
                ExportDiagnosisKeysAsFileUseCase.Params(riskLevels)
            ) {
                success { _shareDiagnosisFile.send(it) }
                failure { handleStatus(it) }
            }
        } else {
            observeStatus(
                startUploadDiagnosisKeysWorkUseCase,
                StartUploadDiagnosisKeysWorkUseCase.Params(riskLevels)
            )
        }
    }
}