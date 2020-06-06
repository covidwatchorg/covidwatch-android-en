package org.covidwatch.android.di

import org.koin.dsl.module

val flavorSpecificModule = module {
    viewModel {
        NotifyOthersViewModel(
            startUploadDiagnosisKeysWorkUseCase = get(),
            enManager = get(),
            positiveDiagnosisRepository = get()
        )
    }
}