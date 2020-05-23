package org.covidwatch.android.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationClient
import com.google.android.gms.safetynet.SafetyNet
import com.google.common.io.BaseEncoding
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import okhttp3.logging.HttpLoggingInterceptor.Level.NONE
import org.covidwatch.android.BuildConfig
import org.covidwatch.android.R
import org.covidwatch.android.data.*
import org.covidwatch.android.data.countrycode.CountryCodeRepository
import org.covidwatch.android.data.diagnosiskeystoken.DiagnosisKeysTokenLocalSource
import org.covidwatch.android.data.diagnosiskeystoken.DiagnosisKeysTokenRepository
import org.covidwatch.android.data.exposureinformation.ExposureInformationLocalSource
import org.covidwatch.android.data.exposureinformation.ExposureInformationRepository
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisLocalSource
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRemoteSource
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
import org.covidwatch.android.data.pref.PreferenceStorage
import org.covidwatch.android.data.pref.SharedPreferenceStorage
import org.covidwatch.android.domain.*
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.exposurenotification.FakeExposureNotification
import org.covidwatch.android.ui.exposurenotification.ExposureNotificationViewModel
import org.covidwatch.android.ui.exposures.ExposuresViewModel
import org.covidwatch.android.ui.home.HomeViewModel
import org.covidwatch.android.ui.onboarding.EnableExposureNotificationsViewModel
import org.covidwatch.android.ui.settings.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.security.SecureRandom

val appModule = module {
    single {
        Nearby.getExposureNotificationClient(androidApplication())
    }

    single {
        ExposureNotificationManager(
            exposureNotification = get()
        )
    }

    single { SafetyNet.getClient(androidApplication()) }

    single {
        SafetyNetManager(
            apiKey = androidContext().getString(R.string.safetynet_api_key),
            packageName = androidContext().packageName,
            safetyNet = get()
        )
    }

    viewModel {
        ExposureNotificationViewModel(
            enManager = get(),
            uploadDiagnosisKeysUseCase = get(),
            provideDiagnosisKeysUseCase = get(),
            updateExposureInformationUseCase = get(),
            exposureInformationRepository = get(),
            preferenceStorage = get()
        )
    }

    viewModel {
        ExposuresViewModel(
            enManager = get(),
            updateExposureInformationUseCase = get(),
            preferenceStorage = get(),
            exposureInformationRepository = get()
        )
    }

    single { WorkManager.getInstance(androidApplication()) }

    single { AppCoroutineDispatchers() }

    single<PreferenceStorage> { SharedPreferenceStorage(androidApplication()) }

    single { PositiveDiagnosisRemoteSource(httpClient = get()) }
    single { PositiveDiagnosisLocalSource() }
    single { PositiveDiagnosisRepository(remote = get(), local = get()) }

    single {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java, "database.db"
        ).fallbackToDestructiveMigration().build()
    }
    single { ExposureInformationLocalSource(database = get()) }
    single { ExposureInformationRepository(local = get()) }

    single {
        val appDatabase: AppDatabase = get()
        appDatabase.diagnosisKeysTokenDao()
    }
    single { DiagnosisKeysTokenLocalSource(keysTokenDao = get()) }
    single { DiagnosisKeysTokenRepository(local = get()) }

    single {
        val appDatabase: AppDatabase = get()
        appDatabase.countryCodeDao()
    }
    single { CountryCodeRepository(local = get()) }

    single { UriManager(serverEndpoint = androidContext().getString(R.string.server_endpoint)) }

    factory {
        ProvideDiagnosisKeysUseCase(
            workManager = get(),
            dispatchers = get()
        )
    }

    factory {
        UploadDiagnosisKeysUseCase(
            enManager = get(),
            diagnosisRepository = get(),
            countryCodeRepository = get(),
            safetyNetManager = get(),
            uriManager = get(),
            appPackageName = androidContext().packageName,
            random = SecureRandom(),
            encoding = BaseEncoding.base64(),
            dispatchers = get()
        )
    }

    factory {
        UpdateExposureStateUseCase(
            workManager = get(),
            dispatchers = get()
        )
    }

    factory {
        UpdateExposureInformationUseCase(
            exposureNotificationManager = get(),
            tokenRepository = get(),
            exposureInformationRepository = get(),
            dispatchers = get()
        )
    }

    factory {
        UserFlowRepository(
            prefs = get()
        )
    }

    factory {
        val context = androidContext()

        context.getSharedPreferences(
            "org.covidwatch.android.PREFERENCE_FILE_KEY",
            Context.MODE_PRIVATE
        )
    }

    viewModel {
        HomeViewModel(
            userFlowRepository = get(),
            testedRepository = get(),
            preferenceStorage = get()
        )
    }

    viewModel {
        SettingsViewModel(androidApplication())
    }

    single {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(if (BuildConfig.DEBUG) BODY else NONE)

        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    single<TestedRepository> {
        TestedRepositoryImpl(
            preferences = get()
        )
    }

    // Onboarding start

    viewModel {
        EnableExposureNotificationsViewModel(exposureNotificationManager = get())
    }

    // Onboarding end
}