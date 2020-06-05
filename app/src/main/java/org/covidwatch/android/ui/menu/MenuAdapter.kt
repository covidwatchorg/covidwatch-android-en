package org.covidwatch.android.ui.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.covidwatch.android.R

class MenuAdapter(
    private val onClick: ((destination: Destination) -> Unit)
) : RecyclerView.Adapter<MenuItemViewHolder>() {

    private var possibleExposuresMenuItem = MenuItem(
        R.string.menu_possible_exposures,
        0,
        PossibleExposures
    )

    private val items = listOf(
        possibleExposuresMenuItem,
        MenuItem(R.string.menu_notify_others, 0, NotifyOthers),
        MenuItem(R.string.menu_how_it_works, 0, HowItWorks),
        MenuItem(
            R.string.menu_health_guidelines,
            R.drawable.ic_exit_to_app,
            Browser("https://www.cdc.gov/coronavirus/2019-ncov/index.html")
        ),
        MenuItem(
            R.string.menu_covid_watch_website,
            R.drawable.ic_exit_to_app,
            Browser("https://www.covid-watch.org/")
        ),
        MenuItem(
            R.string.menu_faq,
            R.drawable.ic_exit_to_app,
            Browser("https://www.covid-watch.org/faq")
        ),
        MenuItem(
            R.string.menu_terms_of_use,
            R.drawable.ic_exit_to_app,
            Browser("https://www.covid-watch.org/privacy")
        ),
        MenuItem(
            R.string.menu_support,
            R.drawable.ic_exit_to_app,
            Browser("https://www.covid-watch.org/support")
        )
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
        return MenuItemViewHolder(root)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        val menuItem = items[position]
        holder.bind(menuItem)

        holder.itemView.setOnClickListener {
            onClick(menuItem.destination)
        }
    }
}