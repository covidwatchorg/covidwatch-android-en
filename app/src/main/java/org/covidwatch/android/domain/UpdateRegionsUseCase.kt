package org.covidwatch.android.domain

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import org.covidwatch.android.BuildConfig
import org.covidwatch.android.data.model.Region
import org.covidwatch.android.data.model.Regions
import org.covidwatch.android.data.pref.PreferenceStorage
import org.covidwatch.android.exposurenotification.Failure
import org.covidwatch.android.exposurenotification.ServerException
import org.covidwatch.android.functional.Either
import timber.log.Timber

@Suppress("BlockingMethodInNonBlockingContext")
class UpdateRegionsUseCase(
    private val httpClient: OkHttpClient,
    private val preferences: PreferenceStorage,
    private val gson: Gson,
    dispatchers: AppCoroutineDispatchers
) : UseCase<Unit, Unit>(dispatchers) {

    override suspend fun run(params: Unit?): Either<Failure, Unit> {
        try {
            val request = Request.Builder().url(BuildConfig.REGIONS_JSON).build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) throw ServerException(response.body?.string())

            val regionsType = object : TypeToken<List<Region?>?>() {}.type

            val regions: List<Region> = gson.fromJson(response.body?.charStream(), regionsType)
            preferences.regions =
                Regions(regions)
        } catch (e: Exception) {
            Timber.d("Failed to update regions data")
            Timber.e(e)
            return Either.Left(Failure(e))
        }

        return Either.Right(Unit)
    }

    data class Params(val token: String)
}