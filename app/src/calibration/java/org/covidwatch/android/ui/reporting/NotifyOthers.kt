package org.covidwatch.android.ui.reporting

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.core.text.HtmlCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.nearby.exposurenotification.TemporaryExposureKey
import kotlinx.coroutines.launch
import org.covidwatch.android.BuildConfig
import org.covidwatch.android.R
import org.covidwatch.android.data.PositiveDiagnosisReport
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
import org.covidwatch.android.databinding.DialogRiskLevelsBinding
import org.covidwatch.android.domain.ExportDiagnosisKeysAsFileUseCase
import org.covidwatch.android.domain.StartUploadDiagnosisKeysWorkUseCase
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.exposurenotification.ExposureNotificationManager.Companion.PERMISSION_KEYS_REQUEST_CODE
import org.covidwatch.android.exposurenotification.ExposureNotificationManager.Companion.PERMISSION_START_REQUEST_CODE
import org.covidwatch.android.extension.launchUseCase
import org.covidwatch.android.extension.observeEvent
import org.covidwatch.android.extension.send
import org.covidwatch.android.ui.event.Event
import java.io.File
import java.net.URLConnection
import java.util.*

class NotifyOthersFragment : BaseNotifyOthersFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewModel) {
            observeEvent(setTransmissionLevelRisk) { riskLevels ->
                context?.let { context ->
                    val dialogView = DialogRiskLevelsBinding.inflate(
                        LayoutInflater.from(context)
                    )
                    val keysNumber = riskLevels.size

                    dialogView.title.text =
                        getString(R.string.transmission_risk_dialog_title, keysNumber)

                    dialogView.description.text =
                        HtmlCompat.fromHtml(
                            getString(R.string.transmission_risk_dialog_message, keysNumber),
                            HtmlCompat.FROM_HTML_MODE_COMPACT
                        )

                    dialogView.etRiskLevels.filters =
                        arrayOf(InputFilter.LengthFilter(keysNumber * 2))
                    dialogView.etRiskLevels.setText(riskLevelsAsString(riskLevels))

                    val dialog = AlertDialog
                        .Builder(context)
                        .setView(dialogView.root)
                        .setPositiveButton(R.string.btn_continue, null)
                        .setNegativeButton(R.string.cancel, null)
                        .create()

                    dialog.show()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val risksLevels = dialogView.etRiskLevels.text.toString()
                        if (risksLevels.isEmpty()) {
                            Toast.makeText(
                                context,
                                "Transmission risk level string can't be empty",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            dialog.dismiss()
                            viewModel.shareReport(risksLevels)
                        }
                    }
                }
            }
            observeEvent(chooseShareMethod) {
                context?.let { context ->
                    val dialogView =
                        org.covidwatch.android.databinding.DialogChooseExportDiagnosisTypeBinding.inflate(
                            LayoutInflater.from(context)
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
            val uri = FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".fileprovider",
                zip
            )
            ShareCompat.IntentBuilder.from(requireActivity())
                .setStream(uri)
                .setType(URLConnection.guessContentTypeFromName(zip.name))
                .startChooser()
        }
    }
}

class NotifyOthersViewModel(
    private val exportDiagnosisKeysAsFileUseCase: ExportDiagnosisKeysAsFileUseCase,
    private val startUploadDiagnosisKeysWorkUseCase: StartUploadDiagnosisKeysWorkUseCase,
    private val enManager: ExposureNotificationManager
) : BaseNotifyOthersViewModel() {
    private val keys: MutableList<TemporaryExposureKey> = mutableListOf()
    private val defaultRiskLevel = 6
    private val riskLevelSeparator = " "
    private val riskLevels = mutableListOf<Int>()

    private val _setTransmissionLevelRisk = MutableLiveData<Event<List<Int>>>()
    val setTransmissionLevelRisk: LiveData<Event<List<Int>>> = _setTransmissionLevelRisk

    private val _shareDiagnosisFile = MutableLiveData<Event<List<File>>>()
    val shareDiagnosisFile: LiveData<Event<List<File>>> = _shareDiagnosisFile

    private val _chooseShareMethod = MutableLiveData<Event<Unit>>()
    val chooseShareMethod: LiveData<Event<Unit>> = _chooseShareMethod

    fun riskLevelsAsString(riskLevels: List<Int>) =
        riskLevels.joinToString(separator = riskLevelSeparator)

    private fun stringToRiskLevels(riskLevels: String) =
        riskLevels.trim().split(riskLevelSeparator).map { it.toInt() }

    fun shareReport(riskLevels: String) {
        // TODO: 03.06.2020 Add validation that check correct format of the string
        this.riskLevels.clear()
        this.riskLevels.addAll(stringToRiskLevels(riskLevels))

        // We skip dialog to choose the method because calibration build doesn't have the validation logic
        shareReportAs(file = true)
    }

    override fun sharePositiveDiagnosis() {
        viewModelScope.launch {
            enManager.isEnabled().success { enabled ->
                if (enabled) {
                    shareReport()
                } else {
                    withPermission(PERMISSION_START_REQUEST_CODE) {
                        enManager.start().apply {
                            success { shareReport() }
                            failure { handleStatus(it) }
                        }
                    }
                }
            }
        }
    }

    private suspend fun shareReport() {
        withPermission(PERMISSION_KEYS_REQUEST_CODE) {
            enManager.temporaryExposureKeyHistory().apply {
                success {
                    keys.clear()
                    keys.addAll(it)
                    onTekHistory(it)
                }
                failure { handleStatus(it) }
            }
        }
    }

    private fun onTekHistory(teks: MutableList<TemporaryExposureKey>) {
        _setTransmissionLevelRisk.send(teks.map { defaultRiskLevel })
    }

    fun shareReportAs(file: Boolean) {
        if (file) {
            viewModelScope.launchUseCase(
                exportDiagnosisKeysAsFileUseCase,
                ExportDiagnosisKeysAsFileUseCase.Params(riskLevels, keys)
            ) {
                success { _shareDiagnosisFile.send(it) }
                failure { handleStatus(it) }
            }
        } else {
            observeStatus(
                startUploadDiagnosisKeysWorkUseCase,
                StartUploadDiagnosisKeysWorkUseCase.Params(
                    keys,
                    PositiveDiagnosisReport(verified = true, reportDate = Date())
                )
            )
        }
    }
}