package org.covidwatch.android.ui.menu

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import org.covidwatch.android.R
import org.covidwatch.android.data.CovidExposureSummary
import org.covidwatch.android.data.exposureinformation.ExposureInformationRepository
import org.covidwatch.android.data.pref.SharedPreferenceStorage
import org.koin.android.ext.android.inject
import androidx.lifecycle.Observer

class MenuFragment : Fragment(R.layout.fragment_menu) {

    val exposureInformationRepository: ExposureInformationRepository by inject()

    val exposureSummary: LiveData<CovidExposureSummary>
        get() = SharedPreferenceStorage(requireContext()).observableExposureSummary

    private var safeMaximumRiskScore = 6
    private var defaultPossibleExposuresPosition = 2

    private lateinit var saveMenuList: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exposureSummary.observe(viewLifecycleOwner, Observer(::updatePossibleExposureMenuItem))

        val menuList: RecyclerView = view.findViewById(R.id.menu_list)
        menuList.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        menuList.adapter = MenuAdapter { destination ->
            handleMenuItemClick(destination)
        }

        saveMenuList = menuList

        val closeButton: ImageView = view.findViewById(R.id.close_button)
        closeButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun handleMenuItemClick(destination: Destination) {
        when (destination) {
            is Settings -> findNavController().navigate(R.id.settingsFragment)
            is TestResults -> {
            }
            is MakeTestExposureNotification -> makeTestExposureNotification()
            is Browser -> openBrowser(destination.url)
        }
    }

    private fun openBrowser(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    private fun makeTestExposureNotification() {
        exposureInformationRepository.addFakeItem()
        findNavController().navigate(R.id.homeFragment)
    }

    //Set red menu icon visible if maximum risk score > safe risk score
    private fun updatePossibleExposureMenuItem(expSummary: CovidExposureSummary) {
        val menuListAdapter: MenuAdapter = saveMenuList.adapter as MenuAdapter
        var position = defaultPossibleExposuresPosition
        //Find the "possible exposures" menu item
        var size = menuListAdapter.getItemCount()
        for (i in 0 until size) {
            if (menuListAdapter.getMenuItem(i).title == R.string.possible_exposures) {
                position = i
                break
            }
        }
        if (expSummary.maximumRiskScore <= safeMaximumRiskScore) {
            menuListAdapter.getMenuItem(position).iconEnd = 0
        } else {
            menuListAdapter.getMenuItem(position).iconEnd = R.drawable.ic_info_red
        }
    }

}
