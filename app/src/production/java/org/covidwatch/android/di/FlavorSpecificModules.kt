package org.covidwatch.android.di

import org.covidwatch.android.ui.reporting.NotifyOthersViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val flavorSpecificModule = module {
    viewModel {
        NotifyOthersViewModel(
            positiveDiagnosisRepository = get()
        )
    }
}