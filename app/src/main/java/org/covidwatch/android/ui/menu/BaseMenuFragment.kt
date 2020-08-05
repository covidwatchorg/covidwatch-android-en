package org.covidwatch.android.ui.menu

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.gson.annotations.Expose
import org.covidwatch.android.R
import org.covidwatch.android.data.CovidExposureInformation
import org.covidwatch.android.databinding.FragmentMenuBinding
import org.covidwatch.android.extension.observe
import org.covidwatch.android.ui.BaseViewModelFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

data class PossibleExposuresJson(
    @Expose
    val exposureConfiguration: CovidExposureConfiguration,
    @Expose
    val exposures: List<CovidExposureInformation>
)

@Suppress("ArrayInDataClass")
data class CovidExposureConfiguration(
    @Expose
    val minimumRiskScore: Int,
    @Expose
    val attenuationScores: IntArray,
    @Expose
    val attenuationWeight: Int?,
    @Expose
    val daysSinceLastExposureScores: IntArray,
    @Expose
    val daysSinceLastExposureWeight: Int?,
    @Expose
    val durationScores: IntArray,
    @Expose
    val durationWeight: Int?,
    @Expose
    val transmissionRiskScores: IntArray,
    @Expose
    val transmissionRiskWeight: Int?,
    @Expose
    val attenuationDurationThresholds: IntArray,
    @Expose
    val attenuationDurationThresholdList: List<IntArray>? = null
)

fun org.covidwatch.android.data.CovidExposureConfiguration.asCovidExposureConfiguration() =
    CovidExposureConfiguration(
        minimumRiskScore,
        attenuationScores,
        attenuationWeight,
        daysSinceLastExposureScores,
        daysSinceLastExposureWeight,
        durationScores,
        durationWeight,
        transmissionRiskScores,
        transmissionRiskWeight,
        durationAtAttenuationThresholds
    )

open class BaseMenuFragment : BaseViewModelFragment<FragmentMenuBinding, MenuViewModel>() {
    override val viewModel: MenuViewModel by viewModel()

    override fun bind(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentMenuBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = MenuAdapter { handleMenuItemClick(it) }
        with(binding) {
            menuList.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )

            menuList.adapter = adapter
            closeButton.setOnClickListener { findNavController().popBackStack() }
        }

        with(viewModel) {
            observe(regionDisabled) { adapter.showShareDiagnosis(!it) }
            observe(highRiskExposure) {
                if (it) {
                    adapter.showHighRiskPossibleExposures()
                } else {
                    adapter.showNoRiskPossibleExposures()
                }
            }
        }
    }

    open fun handleMenuItemClick(menuItem: MenuItem) {
        when (menuItem.destination) {
            is Browser -> openBrowser(menuItem.destination.url)
            PossibleExposures -> findNavController().navigate(R.id.exposuresFragment)
            NotifyOthers -> findNavController().navigate(R.id.notifyOthersFragment)
            HowItWorks -> findNavController().navigate(R.id.onboardingFragment)
            PastDiagnoses -> findNavController().navigate(R.id.positiveDiagnosesFragment)
            ChangeRegion -> findNavController().navigate(R.id.selectRegionFragment)
        }
    }

    private fun openBrowser(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }
}
