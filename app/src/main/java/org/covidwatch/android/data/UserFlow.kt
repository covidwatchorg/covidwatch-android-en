package org.covidwatch.android.data

sealed class UserFlow

object FirstTimeUser : UserFlow()

object ReturnUser : UserFlow()