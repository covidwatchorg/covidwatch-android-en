package org.covidwatch.android.ui.menu

import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.nearby.exposurenotification.ExposureConfiguration
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.covidwatch.android.R
import org.covidwatch.android.data.exposureinformation.ExposureInformationRepository
import org.covidwatch.android.data.pref.PreferenceStorage
import org.covidwatch.android.databinding.DialogExposureConfigurationBinding
import org.covidwatch.android.domain.ProvideDiagnosisKeysUseCase
import org.covidwatch.android.extension.observeUseCase
import org.koin.android.ext.android.inject

class MenuFragment : BaseMenuFragment() {
    private val exposureInformationRepository: ExposureInformationRepository by inject()
    private val provideDiagnosisKeysUseCase: ProvideDiagnosisKeysUseCase by inject()
    private val preferences: PreferenceStorage by inject()

    override fun handleMenuItemClick(menuItem: MenuItem) {
        super.handleMenuItemClick(menuItem)
        when (menuItem.title) {
            R.string.menu_reset_possible_exposures -> {
                lifecycleScope.launch { exposureInformationRepository.reset() }
                Toast.makeText(context, "Possible exposures were deleted", Toast.LENGTH_SHORT)
                    .show()
                findNavController().popBackStack()
            }
            R.string.menu_detect_exposures_from_server -> {
                lifecycleScope.observeUseCase(provideDiagnosisKeysUseCase) {
                    success {
                        Toast.makeText(
                            context,
                            "Positive diagnosis downloaded",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                    failure { handleStatus(it) }
                    findNavController().popBackStack()
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
                        setNumber(attenuationWeight, configuration.attenuationWeight)
                        setArray(
                            daysSinceLastExposureScores,
                            configuration.daysSinceLastExposureScores
                        )
                        setNumber(
                            daysSinceLastExposureWeight,
                            configuration.daysSinceLastExposureWeight
                        )
                        setArray(durationScores, configuration.durationScores)
                        setNumber(durationWeight, configuration.durationWeight)
                        setArray(transmissionRiskScores, configuration.transmissionRiskScores)
                        setNumber(transmissionRiskWeight, configuration.transmissionRiskWeight)
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
                preferences.exposureConfiguration = configuration
                return true
            } catch (e: Exception) {
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
                R.string.menu_set_exposure_configuration,
                0,
                Destination.None
            )
        )

        items.addAll(0, debugItems)
    }
}