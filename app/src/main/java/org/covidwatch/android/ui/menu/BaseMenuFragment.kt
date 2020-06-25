package org.covidwatch.android.ui.menu

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import org.covidwatch.android.R
import org.covidwatch.android.databinding.FragmentMenuBinding
import org.covidwatch.android.extension.observe
import org.covidwatch.android.ui.BaseViewModelFragment
import org.koin.android.ext.android.inject


open class BaseMenuFragment : BaseViewModelFragment<FragmentMenuBinding, MenuViewModel>() {

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
    }

    private fun openBrowser(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }
}
