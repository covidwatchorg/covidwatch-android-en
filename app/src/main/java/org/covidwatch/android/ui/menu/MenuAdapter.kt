package org.covidwatch.android.ui.menu

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.covidwatch.android.R
import org.covidwatch.android.data.pref.SharedPreferenceStorage

class MenuAdapter(
    private val onClick: ((destination: Destination) -> Unit)
) : RecyclerView.Adapter<MenuItemViewHolder>() {

    private val items = listOf(
        MenuItem(
            R.string.generate_random_exposure,
            R.drawable.ic_exit_to_app,
            MakeTestExposureNotification
        ),
        MenuItem(
            R.string.settings,
            0,
            Settings
        ),
        MenuItem(
            R.string.possible_exposures,
            R.drawable.ic_info_red,
            TestResults
        ),
        MenuItem(
            R.string.notify_others,
            0,
            Browser("https://www.covid-watch.org/")
        ),
        MenuItem(
            R.string.how_the_app_works,
            0,
            Browser("https://www.covid-watch.org/")
        ),
        MenuItem(
            R.string.health_guidelines,
            R.drawable.ic_exit_to_app,
            Browser("https://www.covid-watch.org/")
        ),
        MenuItem(
            R.string.covid_watch_website,
            R.drawable.ic_exit_to_app,
            Browser("https://www.covid-watch.org/")
        ),
        MenuItem(
            R.string.faq,
            R.drawable.ic_exit_to_app,
            Browser("https://www.covid-watch.org/")
        ),
        MenuItem(
            R.string.terms_of_use,
            R.drawable.ic_exit_to_app,
            Browser("https://www.covid-watch.org/")
        ),
        MenuItem(
            R.string.privacy_policy,
            R.drawable.ic_exit_to_app,
            Browser("https://www.covid-watch.org/")
        ),
        MenuItem(
            R.string.get_support,
            R.drawable.ic_exit_to_app,
            Browser("https://www.covid-watch.org/")
        )
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
        return MenuItemViewHolder(root)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        var menuItem = items[position]

        holder.bind(menuItem)

        holder.itemView.setOnClickListener {
            onClick(menuItem.destination)
        }

    }

    fun getMenuItem(i: Int): MenuItem{
        return items[i]
    }

}