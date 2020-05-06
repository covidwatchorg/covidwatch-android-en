package org.covidwatch.android.data

sealed class UserFlow

object FirstTimeUser : UserFlow()

object Setup : UserFlow()

object ReturnUser : UserFlow()