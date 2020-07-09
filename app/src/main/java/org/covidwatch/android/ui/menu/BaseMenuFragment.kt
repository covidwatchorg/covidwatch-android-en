package org.covidwatch.android.ui.menu

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.coroutines.launch
import org.covidwatch.android.R
import org.covidwatch.android.data.exposureinformation.ExposureInformationRepository
import org.covidwatch.android.data.pref.PreferenceStorage
import org.covidwatch.android.databinding.FragmentMenuBinding
import org.covidwatch.android.domain.ProvideDiagnosisKeysUseCase
import org.covidwatch.android.extension.observe
import org.covidwatch.android.extension.observeUseCase
import org.covidwatch.android.ui.BaseViewModelFragment
import org.koin.android.ext.android.inject


open class BaseMenuFragment : BaseViewModelFragment<FragmentMenuBinding, MenuViewModel>() {
    private val exposureInformationRepository: ExposureInformationRepository by inject()
    private val provideDiagnosisKeysUseCase: ProvideDiagnosisKeysUseCase by inject()
    private val preferences: PreferenceStorage by inject()

    override val viewModel: MenuViewModel by inject()

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

        observe(viewModel.highRiskExposure) {
            if (it) {
                adapter.showHighRiskPossibleExposures()
            } else {
                adapter.showNoRiskPossibleExposures()
            }
        }
    }

    open fun handleMenuItemClick(menuItem: MenuItem) {
        when (menuItem.destination) {
            is PossibleExposures -> findNavController().navigate(R.id.exposuresFragment)
            is NotifyOthers -> findNavController().navigate(R.id.notifyOthersFragment)
            is HowItWorks -> findNavController().navigate(R.id.onboardingFragment)
            is Browser -> openBrowser(getString(menuItem.destination.url))
        }
        when (menuItem.title) {
            R.string.menu_reset_possible_exposures -> {
                lifecycleScope.launch {
                    exposureInformationRepository.reset()
                    preferences.resetExposureSummary()
                }
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
        }
    }

    private fun openBrowser(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }
}
