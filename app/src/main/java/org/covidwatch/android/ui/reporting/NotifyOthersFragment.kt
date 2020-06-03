package org.covidwatch.android.ui.reporting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.covidwatch.android.R
import org.covidwatch.android.databinding.DialogChooseExportDiagnosisTypeBinding
import org.covidwatch.android.databinding.DialogRiskLevelsBinding
import org.covidwatch.android.databinding.FragmentNotifyOthersBinding
import org.covidwatch.android.extension.observe
import org.covidwatch.android.extension.observeEvent
import org.covidwatch.android.ui.BaseViewModelFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.net.URLConnection


class NotifyOthersFragment :
    BaseViewModelFragment<FragmentNotifyOthersBinding, NotifyOthersViewModel>() {

    override val viewModel: NotifyOthersViewModel by viewModel()

    private val adapter = PositiveDiagnosisAdapter()

    override fun bind(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNotifyOthersBinding =
        FragmentNotifyOthersBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            closeButton.setOnClickListener {
                findNavController().popBackStack()
            }
            sharePositiveDiagnosisButton.setOnClickListener {
                viewModel.sharePositiveDiagnosis()
            }

            pastPositiveDiagnosesList.addItemDecoration(dividerItemDecoration())
            pastPositiveDiagnosesList.adapter = adapter
        }

        with(viewModel) {
            observe(positiveDiagnosis) { adapter.setItems(it) }
            observeEvent(setTransmissionLevelRisk) { riskLevels ->
                context?.let { context ->
                    val dialogView = DialogRiskLevelsBinding.inflate(
                        LayoutInflater.from(context)
                    )
                    dialogView.etRiskLevels.setText(riskLevelsAsString(riskLevels))

                    AlertDialog
                        .Builder(context)
                        .setView(dialogView.root)
                        .setPositiveButton(R.string.continue_upload) { _, _ ->
                            // TODO: 03.06.2020 Add validation that check correct format of the string
                            val risksLevels = dialogView.etRiskLevels.text.toString()
                            viewModel.shareReport(risksLevels)
                        }
                        .setNegativeButton(R.string.cancel, null)
                        .create()
                        .show()
                }
            }
            observeEvent(chooseShareMethod) {
                context?.let { context ->
                    val dialogView =
                        DialogChooseExportDiagnosisTypeBinding.inflate(LayoutInflater.from(context))

                    val dialog = BottomSheetDialog(context)
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
                context.packageName + ".fileprovider",
                zip
            )
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.type = URLConnection.guessContentTypeFromName(zip.name)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(shareIntent, null))
        }
    }

    private fun dividerItemDecoration(): RecyclerView.ItemDecoration {
        return DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
    }
}