package org.covidwatch.android.ui.menu

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.GsonBuilder
import com.jaredrummler.android.device.DeviceName
import kotlinx.coroutines.launch
import org.covidwatch.android.BuildConfig
import org.covidwatch.android.R
import org.covidwatch.android.attenuationDurationThresholds
import org.covidwatch.android.data.exposureinformation.ExposureInformationRepository
import org.covidwatch.android.data.keyfile.KeyFileRepository
import org.covidwatch.android.data.pref.PreferenceStorage
import org.covidwatch.android.databinding.DialogPossibleExposuresTestCaseBinding
import org.covidwatch.android.domain.ProvideDiagnosisKeysUseCase
import org.covidwatch.android.extension.launchUseCase
import org.koin.android.ext.android.inject
import java.io.File
import java.time.Instant


class MenuFragment : BaseMenuFragment() {
    private val exposureInformationRepository: ExposureInformationRepository by inject()
    private val keyFileRepository: KeyFileRepository by inject()
    private val provideDiagnosisKeysUseCase: ProvideDiagnosisKeysUseCase by inject()
    private val preferences: PreferenceStorage by inject()

    override fun handleMenuItemClick(menuItem: MenuItem) {
        super.handleMenuItemClick(menuItem)
        when (menuItem.title) {
            R.string.menu_reset_possible_exposures -> {
                lifecycleScope.launch {
                    exposureInformationRepository.reset()
                    keyFileRepository.reset()
                    preferences.riskMetrics = null

                    Toast.makeText(context, "Possible exposures were deleted", Toast.LENGTH_SHORT)
                        .show()
                    findNavController().popBackStack()
                }
            }
            R.string.menu_detect_exposures_from_server -> {
                lifecycleScope.launchUseCase(provideDiagnosisKeysUseCase)
                findNavController().popBackStack()
            }
            R.string.menu_export_possible_exposures -> {
                context?.let { context ->
                    val dialogView =
                        DialogPossibleExposuresTestCaseBinding.inflate(LayoutInflater.from(context))
                    val dialog = AlertDialog
                        .Builder(context)
                        .setView(dialogView.root)
                        .setPositiveButton(R.string.btn_continue) { _, _ ->
                            sharePossibleExposures(context, dialogView.testCaseName.text.toString())
                        }
                        .setNegativeButton(R.string.cancel, null)
                        .create()
                    dialog.show()

                }
            }
        }
    }

    private fun sharePossibleExposures(context: Context, testCaseName: String) {
        lifecycleScope.launch {
            // Create JSON of possible exposures
            val exposures = exposureInformationRepository.exposures()
            val configuration = preferences.exposureConfiguration.asCovidExposureConfiguration()

            val possibleExposuresJson = PossibleExposuresJson(
                configuration.copy(attenuationDurationThresholdList = attenuationDurationThresholds),
                exposures
            )

            val json = GsonBuilder()
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .create()
                .toJson(possibleExposuresJson)

            // Write Json to a file
            val deviceName = DeviceName.getDeviceName()
                .trim()
                .replace(" ", "_")
                .replace("/", "_")

            val fileName =
                "${deviceName}_${Instant.now()}_$testCaseName.json"
            val file = File(context.filesDir, fileName)

            json.byteInputStream().copyTo(file.outputStream())
            val uri = FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".fileprovider",
                file
            )

            // Share the file
            ShareCompat.IntentBuilder.from(requireActivity())
                .setEmailTo(arrayOf("calibration-test@covidwatch.org"))
                .setSubject(fileName)
                .setStream(uri)
                .setType("text/json")
                .startChooser()
        }
    }
}

class MenuAdapter(onClick: (menuItem: MenuItem) -> Unit) : BaseMenuAdapter(onClick) {

    init {
        val debugItems = listOf(
            MenuItem(
                R.string.menu_reset_possible_exposures,
                0,
                Destination.None
            ),
            MenuItem(
                R.string.menu_detect_exposures_from_server,
                0,
                Destination.None
            ),
            MenuItem(
                R.string.menu_export_possible_exposures,
                0,
                Destination.None
            )
        )

        items.addAll(0, debugItems)
    }
}