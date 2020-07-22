package org.covidwatch.android.di

import org.covidwatch.android.domain.ExportDiagnosisKeysAsFileUseCase
import org.covidwatch.android.domain.ProvideDiagnosisKeysFromFileUseCase
import org.covidwatch.android.exposurenotification.KeyFileSigner
import org.covidwatch.android.exposurenotification.KeyFileWriter
import org.covidwatch.android.ui.reporting.NotifyOthersViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val flavorSpecificModule = module {
    viewModel {
        NotifyOthersViewModel(
            startUploadDiagnosisKeysWorkUseCase = get(),
            exportDiagnosisKeysAsFileUseCase = get(),
            enManager = get()
        )
    }

    single { KeyFileSigner() }
    single { KeyFileWriter(androidApplication(), get()) }
    factory {
        androidApplication().contentResolver
    }
    factory {
        ProvideDiagnosisKeysFromFileUseCase(
            enManager = get(),
            diagnosisKeysTokenRepository = get(),
            contentResolver = get(),
            preferences = get(),
            dispatchers = get()
        )
    }

    factory {
        ExportDiagnosisKeysAsFileUseCase(
            enManager = get(),
            keyFileWriter = get(),
            dispatchers = get()
        )
    }
}