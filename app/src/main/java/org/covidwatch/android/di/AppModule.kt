package org.covidwatch.android.di

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.room.Room
import androidx.work.WorkManager
import com.google.android.gms.nearby.Nearby
import com.google.common.io.BaseEncoding
import com.google.gson.Gson
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
import org.covidwatch.android.data.diagnosisverification.DiagnosisVerificationRemoteSource
import org.covidwatch.android.data.diagnosisverification.DiagnosisVerificationRepository
import org.covidwatch.android.data.exposureinformation.ExposureInformationLocalSource
import org.covidwatch.android.data.exposureinformation.ExposureInformationRepository
import org.covidwatch.android.data.keyfile.KeyFileLocalSource
import org.covidwatch.android.data.keyfile.KeyFileRepository
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisLocalSource
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRemoteSource
import org.covidwatch.android.data.positivediagnosis.PositiveDiagnosisRepository
import org.covidwatch.android.data.pref.PreferenceStorage
import org.covidwatch.android.data.pref.SharedPreferenceStorage
import org.covidwatch.android.data.risklevel.RiskLevelRepository
import org.covidwatch.android.domain.*
import org.covidwatch.android.exposurenotification.ExposureNotificationManager
import org.covidwatch.android.ui.IResourcesProvider
import org.covidwatch.android.ui.Notifications
import org.covidwatch.android.ui.ResourcesProvider
import org.covidwatch.android.ui.exposures.ExposuresViewModel
import org.covidwatch.android.ui.home.HomeViewModel
import org.covidwatch.android.ui.menu.MenuViewModel
import org.covidwatch.android.ui.onboarding.EnableExposureNotificationsViewModel
import org.covidwatch.android.ui.reporting.NotifyOthersViewModel
import org.covidwatch.android.ui.reporting.PositiveDiagnosesViewModel
import org.covidwatch.android.ui.reporting.VerifyPositiveDiagnosisViewModel
import org.covidwatch.android.ui.selectregion.SelectRegionViewModel
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

    single<EnConverter> { ArizonaEnConverter(prefs = get()) }

    single { Notifications(context = androidApplication()) }

    single {
        ExposureNotificationManager(
            exposureNotification = get()
        )
    }

    single {
        DiagnosisVerificationManager(
            verificationRepository = get()
        )
    }

    single<IResourcesProvider> { ResourcesProvider(androidApplication()) }

    single { WorkManager.getInstance(androidApplication()) }

    single { AppCoroutineDispatchers() }

    single<PreferenceStorage> { SharedPreferenceStorage(androidApplication()) }

    single {
        PositiveDiagnosisRemoteSource(
            httpClient = get(),
            keysDir = androidContext().filesDir.absolutePath
        )
    }
    single {
        val appDatabase: AppDatabase = get()
        appDatabase.positiveDiagnosisReportDao()
    }
    single { PositiveDiagnosisLocalSource(reportDao = get()) }
    single {
        PositiveDiagnosisRepository(
            remote = get(),
            local = get(),
            countryCodeRepository = get(),
            uriManager = get(),
            keyFileRepository = get(),
            dispatchers = get()
        )
    }

    single { Gson() }
    single {
        DiagnosisVerificationRemoteSource(
            apiKey = androidContext().getString(R.string.verification_api_key),
            verificationServerEndpoint = androidContext().getString(R.string.server_verification_endpoint),
            gson = get(),
            httpClient = get()
        )
    }
    single {
        DiagnosisVerificationRepository(
            remote = get(),
            dispatchers = get()
        )
    }

    single {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java, "database.db"
        ).fallbackToDestructiveMigration().build()
    }
    single {
        val appDatabase: AppDatabase = get()
        appDatabase.exposureInformationDao()
    }
    single { ExposureInformationLocalSource(dao = get()) }
    single {
        ExposureInformationRepository(
            local = get(),
            dispatchers = get()
        )
    }

    single {
        val appDatabase: AppDatabase = get()
        appDatabase.keyFileDao()
    }
    single { KeyFileLocalSource(dao = get()) }
    single {
        KeyFileRepository(
            local = get(),
            dispatchers = get()
        )
    }

    single {
        val appDatabase: AppDatabase = get()
        appDatabase.diagnosisKeysTokenDao()
    }
    single { DiagnosisKeysTokenLocalSource(keysTokenDao = get()) }
    single {
        DiagnosisKeysTokenRepository(
            local = get(),
            dispatchers = get()
        )
    }

    single {
        val appDatabase: AppDatabase = get()
        appDatabase.countryCodeDao()
    }
    single {
        CountryCodeRepository(
            local = get(),
            dispatchers = get()
        )
    }

    single {
        UriManager(
            serverUploadEndpoint = androidContext().getString(R.string.server_upload_endpoint),
            serverDownloadEndpoint = androidContext().getString(R.string.server_download_endpoint),
            httpClient = get()
        )
    }

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
            verificationManager = get(),
            uriManager = get(),
            enConverter = get(),
            appPackageName = androidContext().packageName,
            random = SecureRandom(),
            encoding = BaseEncoding.base64(),
            dispatchers = get()
        )
    }

    factory {
        StartUploadDiagnosisKeysWorkUseCase(
            workManager = get(),
            dispatchers = get()
        )
    }

    factory {
        RemoveUnverifiedReportsUseCase(
            workManager = get(),
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
        UpdateRegionsUseCase(
            httpClient = get(),
            preferences = get(),
            gson = get(),
            dispatchers = get()
        )
    }

    factory {
        UpdateExposureInformationUseCase(
            enManager = get(),
            tokenRepository = get(),
            exposureInformationRepository = get(),
            enConverter = get(),
            dispatchers = get()
        )
    }

    factory {
        RemoveOldExposuresUseCase(
            workManager = get(),
            dispatchers = get()
        )
    }

    single {
        UserFlowRepository(
            prefs = get()
        )
    }

    single {
        RiskLevelRepository(
            prefs = get(),
            positiveDiagnosisRepository = get(),
            dispatchers = get()
        )
    }

    single {
        val context = androidContext()

        context.getSharedPreferences(
            "org.covidwatch.android.PREFERENCE_FILE_KEY",
            Context.MODE_PRIVATE
        )
    }

    viewModel {
        NotifyOthersViewModel()
    }
    viewModel {
        ExposuresViewModel(
            enManager = get(),
            updateExposureInformationUseCase = get(),
            preferenceStorage = get(),
            exposureInformationRepository = get()
        )
    }
    viewModel {
        HomeViewModel(
            enManager = get(),
            provideDiagnosisKeysUseCase = get(),
            userFlowRepository = get(),
            preferences = get(),
            riskLevelRepository = get()
        )
    }

    viewModel {
        SettingsViewModel(androidApplication())
    }

    viewModel {
        SelectRegionViewModel(preferences = get(), resources = get())
    }

    viewModel {
        MenuViewModel(prefs = get(), exposureInformationRepository = get())
    }

    viewModel {
        PositiveDiagnosesViewModel(positiveDiagnosisRepository = get())
    }

    viewModel { (state: SavedStateHandle) ->
        VerifyPositiveDiagnosisViewModel(
            state = state,
            startUploadDiagnosisKeysWorkUseCase = get(),
            removeUnverifiedReportsUseCase = get(),
            verificationManager = get(),
            positiveDiagnosisRepository = get(),
            enManager = get()
        )
    }

    single {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(if (BuildConfig.DEBUG) BODY else NONE)

        OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(ConnectivityInterceptor(androidApplication()))
            .build()
    }

    // Onboarding start

    viewModel {
        EnableExposureNotificationsViewModel(enManager = get(), userFlowRepository = get())
    }

    // Onboarding end
}