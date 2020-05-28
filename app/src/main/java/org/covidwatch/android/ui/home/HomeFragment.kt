package org.covidwatch.android.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem.SHOW_AS_ACTION_ALWAYS
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import org.covidwatch.android.*
import org.covidwatch.android.data.CovidExposureSummary
import org.covidwatch.android.databinding.FragmentHomeBinding
import org.covidwatch.android.extension.shareApp
import org.covidwatch.android.ui.BaseFragment
import org.covidwatch.android.ui.event.EventObserver
import org.covidwatch.android.ui.exposurenotification.ExposureNotificationActivity
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val homeViewModel: HomeViewModel by viewModel()

    override fun bind(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding =
        FragmentHomeBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Add meta-data test here
        getFirebaseIdIfTester()
        homeViewModel.onStart()
        homeViewModel.navigateToOnboardingEvent.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.splashFragment)
        })

        homeViewModel.exposureSummary.observe(viewLifecycleOwner, Observer(::bindExposureSummary))

        homeViewModel.infoBannerState.observe(viewLifecycleOwner, Observer { banner ->
            when (banner) {
                is InfoBannerState.Visible -> {
                    binding.infoBanner.isVisible = true
                    binding.infoBanner.setText(banner.text)
                }
                InfoBannerState.Hidden -> {
                    binding.infoBanner.isVisible = false
                }
            }
        })
        homeViewModel.warningBannerState.observe(viewLifecycleOwner, Observer { banner ->
            when (banner) {
                is WarningBannerState.Visible -> {
                    binding.warningBanner.isVisible = true
                    binding.warningBanner.setText(banner.text)
                }
                WarningBannerState.Hidden -> {
                    binding.warningBanner.isVisible = false
                }
            }
        })
        homeViewModel.isUserTestedPositive.observe(
            viewLifecycleOwner,
            Observer(::updateUiForTestedPositive)
        )

        initClickListeners()
        addDebugMenuItem()
    }

    private fun addDebugMenuItem() {
        if (BuildConfig.DEBUG) {
            val debugMenu = binding.toolbar.menu.add(
                Menu.NONE,
                Menu.NONE,
                0,
                "Debug"
            )
            debugMenu.setShowAsAction(SHOW_AS_ACTION_ALWAYS)
            debugMenu.icon = context?.getDrawable(R.drawable.ic_debug)
            debugMenu.setOnMenuItemClickListener {
                startActivity(Intent(context, ExposureNotificationActivity::class.java))
                true
            }
        }
    }

    private fun initClickListeners() {
        with(binding) {
            notifyOthersButton.setOnClickListener {
                findNavController().navigate(R.id.notifyOthersFragment)
            }
            toolbar.setOnMenuItemClickListener {
                if (it.itemId == R.id.action_menu) {
                    findNavController().navigate(R.id.menuFragment)
                }
                true
            }

            shareAppButton.setOnClickListener {
                context?.shareApp()
            }
            infoBanner.setOnClickListener {
                findNavController().navigate(R.id.settingsFragment)
            }
            exposureSummary.root.setOnClickListener {
                findNavController().navigate(R.id.exposuresFragment)
            }
        }
    }

    private fun bindExposureSummary(exposureSummary: CovidExposureSummary) {
        binding.exposureSummary.daysSinceLastExposure.text =
            exposureSummary.daySinceLastExposure.toString()
        binding.exposureSummary.totalExposures.text = exposureSummary.matchedKeyCount.toString()
        binding.exposureSummary.highRiskScore.text = exposureSummary.maximumRiskScore.toString()
    }

    private fun updateUiForTestedPositive(isUserTestedPositive: Boolean) {
        binding.notifyOthersButtonQuestion.isVisible = !isUserTestedPositive
        binding.notifyOthersButton.isVisible = !isUserTestedPositive
        binding.notifyOthersButtonText.isVisible = !isUserTestedPositive
    }

    private fun getFirebaseIdIfTester() {
        if (BuildConfig.FIREBASE_DEBUGGING == false) {
            binding.testerId.visibility = View.GONE
            setTester(false)
            return
        } else {
            val firebaseId: String = getFirebaseId()
            binding.testerId.text = "Your COVID Watch Tester Id is: " + firebaseId
            binding.testerId.visibility = View.VISIBLE
            setTester(true)
            val context = requireContext()
            setAnalyticsInstanceFromContext(context)
            //Test event
            sendEvent("TestEvent")
            return
        }
    }
}
