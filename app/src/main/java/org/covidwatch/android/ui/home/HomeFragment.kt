package org.covidwatch.android.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import org.covidwatch.android.*
import org.covidwatch.android.data.CovidExposureSummary
import org.covidwatch.android.data.pref.SharedPreferenceStorage
import org.covidwatch.android.databinding.FragmentHomeBinding
import org.covidwatch.android.exposurenotification.RandomEnObjects
import org.covidwatch.android.ui.BaseFragment
import org.covidwatch.android.ui.event.EventObserver
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.covidwatch.android.BuildConfig


class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val homeViewModel: HomeViewModel by viewModel()
    private var settingsExposureSummary: CovidExposureSummary = CovidExposureSummary(
        daySinceLastExposure = 0,
        matchedKeyCount = 0,
        maximumRiskScore = 0,
        attenuationDurationsInMinutes = intArrayOf(),
        summationRiskScore = 0
    )


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

        if (RandomEnObjects.retrieved == true) {
            var sharedPreferences: SharedPreferenceStorage =
                SharedPreferenceStorage(requireContext())
            settingsExposureSummary = sharedPreferences.exposureSummary
            bindExposureSummary(settingsExposureSummary)
            RandomEnObjects.retrieved = false
        }

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
        homeViewModel.isUserTestedPositive.observe(viewLifecycleOwner, Observer(::updateUiForTestedPositive))

        initClickListeners()
    }

    private fun initClickListeners() {
        binding.notifyOthersButton.setOnClickListener {
            findNavController().navigate(R.id.notifyOthersFragment)
        }
        binding.toolbar.setOnMenuItemClickListener {
            if (R.id.action_menu == it.itemId) {
                findNavController().navigate(R.id.menuFragment)
            }
            true
        }
        binding.shareAppButton.setOnClickListener {
            shareApp()
        }
        binding.infoBanner.setOnClickListener {
            findNavController().navigate(R.id.settingsFragment)
        }
        binding.exposureSummary.root.setOnClickListener {
            findNavController().navigate(R.id.exposuresFragment)
        }
    }

    private fun shareApp() {
        val shareText = getString(R.string.share_intent_text)
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "$shareText https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
        )
        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share_text)))
    }

    private fun bindExposureSummary(exposureSummary: CovidExposureSummary) {
        var newExposureSummary = exposureSummary
        //We want to use the exposureSummary that has the higher matchedKeyCount
        //The other one is lagging
        if (settingsExposureSummary.matchedKeyCount > newExposureSummary.matchedKeyCount) {
            newExposureSummary = settingsExposureSummary
        }
        binding.exposureSummary.daysSinceLastExposure.text = newExposureSummary.daySinceLastExposure.toString()
        binding.exposureSummary.totalExposures.text = newExposureSummary.matchedKeyCount.toString()
        binding.exposureSummary.highRiskScore.text = newExposureSummary.maximumRiskScore.toString()
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