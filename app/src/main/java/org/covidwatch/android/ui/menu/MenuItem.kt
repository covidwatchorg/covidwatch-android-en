package org.covidwatch.android.ui.menu

import androidx.annotation.StringRes

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
object PastDiagnoses : Destination()
object ChangeRegion : Destination()
object HowItWorks : Destination()
class Browser(@StringRes val url: Int) : Destination()