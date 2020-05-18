package org.covidwatch.android.ui.menu

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import org.covidwatch.android.R
import kotlinx.coroutines.GlobalScope
import org.covidwatch.android.data.CovidExposureInformation
import org.covidwatch.android.data.exposureinformation.ExposureInformationRepository
import org.covidwatch.android.data.toCovidExposureInformation
import org.covidwatch.android.exposurenotification.RandomEnObjects
import org.covidwatch.android.extension.io
import org.koin.android.ext.android.inject
import org.covidwatch.android.exposurenotification.*


class MenuFragment : Fragment(R.layout.fragment_menu) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

        val closeButton: ImageView = view.findViewById(R.id.close_button)
        closeButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun handleMenuItemClick(destination: Destination) {
        when (destination) {
            is Settings -> findNavController().navigate(R.id.settingsFragment)
            is TestResults -> {}
            is MakeTestExposureNotification -> makeTestExposureNotification()
            is Browser -> openBrowser(destination.url)
        }
    }

    private fun openBrowser(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    private fun makeTestExposureNotification() {
        val exposureInformationRepository: ExposureInformationRepository by inject()
        var returnExposureInformationList : List<CovidExposureInformation>
        val testExposureNotification = TestExposureNotification()
        val context: Context = requireContext()
        val covidExposureInformation: CovidExposureInformation =
            RandomEnObjects.exposureInformation.toCovidExposureInformation()
        var exposureInformationList: List<CovidExposureInformation> =
            listOf(covidExposureInformation)

        GlobalScope.io {
            returnExposureInformationList = saveOneGetAll(exposureInformationRepository, exposureInformationList)
            //sum up risk exposures from returnExposureInformationList and pass to TestExposureNotification
            testExposureNotification.saveExposureSummaryInPreferences(context,covidExposureInformation,returnExposureInformationList.size)
            findNavController().navigate(R.id.homeFragment)
        }
    }

    //Save the new exposureInformation object to the database
    //Read all the exposureInformation objects from the database into a list
    suspend private fun saveOneGetAll(
        exposureInformationRepository: ExposureInformationRepository,
        exposureInformationList: List<CovidExposureInformation>): List<CovidExposureInformation>
    {
        exposureInformationRepository.saveExposureInformation(exposureInformationList)
        var newExposureInformationList: List<CovidExposureInformation>
        newExposureInformationList = exposureInformationRepository.realExposureInformation()
        return newExposureInformationList
    }
}
