package org.covidwatch.android.ui.menu

data class MenuItem(
    val title: Int,
    val iconEnd: Int,
    val destination: Destination
)

sealed class Destination {
    object None : Destination()
}

object PossibleExposures : Destination()
object NotifyOthers : Destination()
object HowItWorks : Destination()
class Browser(val url: String) : Destination()