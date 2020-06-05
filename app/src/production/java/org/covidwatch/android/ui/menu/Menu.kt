package org.covidwatch.android.ui.menu

import org.covidwatch.android.R
import org.covidwatch.android.data.exposureinformation.ExposureInformationRepository

class MenuFragment : BaseMenuFragment()

class MenuAdapter(onClick: (destination: Destination) -> Unit) : BaseMenuAdapter(onClick)