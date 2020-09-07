package org.covidwatch.android.data.model

sealed class UserFlow

object FirstTimeUser : UserFlow()

object ReturnUser : UserFlow()