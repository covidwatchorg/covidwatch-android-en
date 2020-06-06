package org.covidwatch.android.ui.menu

class MenuFragment : BaseMenuFragment()

class MenuAdapter(onClick: (menuItem: MenuItem) -> Unit) : BaseMenuAdapter(onClick)