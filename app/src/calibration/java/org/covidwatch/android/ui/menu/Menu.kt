package org.covidwatch.android.ui.menu

import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.nearby.exposurenotification.ExposureConfiguration
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.jaredrummler.android.device.DeviceName
import kotlinx.coroutines.launch
import org.covidwatch.android.BuildConfig
import org.covidwatch.android.R
import org.covidwatch.android.attenuationDurationThresholds
import org.covidwatch.android.data.exposureinformation.ExposureInformationRepository
import org.covidwatch.android.data.keyfile.KeyFileRepository
import org.covidwatch.android.data.pref.PreferenceStorage
import org.covidwatch.android.databinding.DialogExposureConfigurationBinding
import org.covidwatch.android.databinding.DialogPossibleExposuresTestCaseBinding
import org.covidwatch.android.domain.ProvideDiagnosisKeysUseCase
import org.covidwatch.android.extension.launchUseCase
import org.koin.android.ext.android.inject
import timber.log.Timber
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
            R.string.menu_set_exposure_configuration -> {
                context?.let { context ->
                    val dialogView = DialogExposureConfigurationBinding.inflate(
                        LayoutInflater.from(context)
                    )
                    val configuration = preferences.exposureConfiguration

                    with(dialogView) {
                        setNumber(minRiskScore, configuration.minimumRiskScore)
                        setArray(attenuationScores, configuration.attenuationScores)
                        setNumber(attenuationWeight, configuration.attenuationWeight ?: 0)
                        setArray(
                            daysSinceLastExposureScores,
                            configuration.daysSinceLastExposureScores
                        )
                        setNumber(
                            daysSinceLastExposureWeight,
                            configuration.daysSinceLastExposureWeight ?: 0
                        )
                        setArray(durationScores, configuration.durationScores)
                        setNumber(durationWeight, configuration.durationWeight ?: 0)
                        setArray(transmissionRiskScores, configuration.transmissionRiskScores)
                        setNumber(transmissionRiskWeight, configuration.transmissionRiskWeight ?: 0)
                        setArray(
                            durationAtAttenuationThresholds,
                            configuration.durationAtAttenuationThresholds
                        )
                    }

                    val dialog = AlertDialog
                        .Builder(context)
                        .setView(dialogView.root)
                        .setTitle("Exposure Configuration")
                        .setPositiveButton(R.string.ok, null)
                        .setNegativeButton(R.string.cancel, null)
                        .create()
                    dialog.show()

                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val saved = setConfiguration(dialogView)
                        if (saved) dialog.dismiss()
                    }
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
            val fileName =
                "${DeviceName.getDeviceName()}_${Instant.now()}_$testCaseName.json"
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

    private fun setConfiguration(dialog: DialogExposureConfigurationBinding): Boolean {
        with(dialog) {
            try {
                val configuration = ExposureConfiguration.ExposureConfigurationBuilder()
                    .setMinimumRiskScore(getNumber(minRiskScore))
                    .setAttenuationScores(*getArray(attenuationScores))
                    .setAttenuationWeight(getNumber(attenuationWeight))
                    .setDaysSinceLastExposureScores(*getArray(daysSinceLastExposureScores))
                    .setAttenuationWeight(getNumber(attenuationWeight))
                    .setDaysSinceLastExposureWeight(getNumber(daysSinceLastExposureWeight))
                    .setDurationScores(*getArray(durationScores))
                    .setDurationWeight(getNumber(durationWeight))
                    .setTransmissionRiskScores(*getArray(transmissionRiskScores))
                    .setTransmissionRiskWeight(getNumber(transmissionRiskWeight))
                    .setDurationAtAttenuationThresholds(*getArray(durationAtAttenuationThresholds))
                    .build()
                // TODO: 22.07.2020 Think what we need from calibration build and if we need it at all
//                preferences.exposureConfiguration = configuration.asCovidExposureConfiguration()
                return true
            } catch (e: Exception) {
                Timber.e(e)
                Snackbar.make(dialog.root, e.message.toString(), LENGTH_LONG).show()
            }
        }
        return false
    }

    private fun setNumber(editText: EditText, number: Int) {
        editText.setText(number.toString())
    }

    private fun getNumber(editText: EditText) = editText.text.toString().toInt()

    private fun setArray(editText: EditText, array: IntArray) {
        editText.setText(array.joinToString(" "))
    }

    private fun getArray(editText: EditText) =
        editText.text.toString().split(" ").map { it.toInt() }.toIntArray()
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
            ),
            MenuItem(
                R.string.menu_set_exposure_configuration,
                0,
                Destination.None
            )
        )

        items.addAll(0, debugItems)
    }
}