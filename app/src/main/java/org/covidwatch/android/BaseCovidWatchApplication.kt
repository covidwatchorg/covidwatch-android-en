package org.covidwatch.android

import android.app.Application
import kotlinx.coroutines.GlobalScope
import org.covidwatch.android.di.appModule
import org.covidwatch.android.di.flavorSpecificModule
import org.covidwatch.android.domain.ProvideDiagnosisKeysUseCase
import org.covidwatch.android.domain.ProvideDiagnosisKeysUseCase.Params
import org.covidwatch.android.domain.UpdateRegionsUseCase
import org.covidwatch.android.extension.launchUseCase
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

open class BaseCovidWatchApplication : Application() {

    private val provideDiagnosisKeysUseCase: ProvideDiagnosisKeysUseCase by inject()
    private val updateRegionsUseCase: UpdateRegionsUseCase by inject()

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        startKoin {
            androidContext(applicationContext)
            modules(appModule, flavorSpecificModule)
        }

        with(GlobalScope) {
            launchUseCase(
                provideDiagnosisKeysUseCase,
                Params(recurrent = true)
            )
            launchUseCase(updateRegionsUseCase)
        }
    }
}
