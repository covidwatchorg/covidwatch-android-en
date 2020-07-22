package org.covidwatch.android.ui.menu

import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.nearby.exposurenotification.ExposureConfiguration
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import org.covidwatch.android.R
import org.covidwatch.android.data.pref.PreferenceStorage
import org.covidwatch.android.databinding.DialogExposureConfigurationBinding
import org.koin.android.ext.android.inject
import timber.log.Timber


class MenuFragment : BaseMenuFragment() {
    private val preferences: PreferenceStorage by inject()

    override fun handleMenuItemClick(menuItem: MenuItem) {
        super.handleMenuItemClick(menuItem)
        when (menuItem.title) {
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
                R.string.menu_set_exposure_configuration,
                0,
                Destination.None
            )
        )

        items.addAll(0, debugItems)
    }
}