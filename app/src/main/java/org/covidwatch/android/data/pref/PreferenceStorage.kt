package org.covidwatch.android.data.pref

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.nearby.exposurenotification.ExposureConfiguration
import com.google.gson.Gson
import org.covidwatch.android.data.CovidExposureSummary
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Storage for app and user preferences.
 */
interface PreferenceStorage {
    var lastFetchDate: Long
    var onboardingFinished: Boolean
    var exposureSummary: CovidExposureSummary

    // TODO: 05.06.2020 Replace with our own class when the API is stable
    var exposureConfiguration: ExposureConfiguration
    val observableExposureSummary: LiveData<CovidExposureSummary>
}

class SharedPreferenceStorage(context: Context) : PreferenceStorage {
    private val prefs = context.applicationContext.getSharedPreferences(NAME, MODE_PRIVATE)
    private val _exposureSummary = MutableLiveData<CovidExposureSummary>()

    private val changeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            EXPOSURE_SUMMARY -> _exposureSummary.value = exposureSummary
        }
    }

    init {
        prefs.registerOnSharedPreferenceChangeListener(changeListener)
    }

    override var lastFetchDate by Preference(prefs, LAST_FETCH_DATE, 0L)

    override var onboardingFinished by Preference(prefs, ONBOARDING_FINISHED, false)

    override var exposureSummary: CovidExposureSummary by ObjectPreference(
        prefs,
        EXPOSURE_SUMMARY,
        CovidExposureSummary(
            daySinceLastExposure = 0,
            matchedKeyCount = 0,
            maximumRiskScore = 0,
            attenuationDurationsInMinutes = intArrayOf(),
            summationRiskScore = 0
        ),
        CovidExposureSummary::class.java
    )

    override var exposureConfiguration: ExposureConfiguration by ObjectPreference(
        prefs,
        EXPOSURE_CONFIGURATION,
        ExposureConfiguration.ExposureConfigurationBuilder()
            .setMinimumRiskScore(1)
            .setDurationAtAttenuationThresholds(58, 73)
            .setAttenuationScores(2, 5, 8, 8, 8, 8, 8, 8)
            .setDaysSinceLastExposureScores(1, 2, 2, 4, 6, 8, 8, 8)
            .setDurationScores(1, 1, 4, 7, 7, 8, 8, 8)
            .setTransmissionRiskScores(0, 3, 6, 8, 8, 6, 0, 6)
            .build(),
        ExposureConfiguration::class.java
    )

    override val observableExposureSummary: LiveData<CovidExposureSummary>
        get() = _exposureSummary.also { it.value = exposureSummary }

    companion object {
        private const val NAME = "ag_minimal_prefs"
        private const val LAST_FETCH_DATE = "last_fetch_date"
        private const val EXPOSURE_SUMMARY = "exposure_summary"
        private const val EXPOSURE_CONFIGURATION = "exposure_configuration"
        private const val ONBOARDING_FINISHED = "onboarding_finished"
    }
}

class ObjectPreference<T>(
    private val preferences: SharedPreferences,
    private val name: String,
    private val defaultValue: T,
    private val clazz: Class<T>,
    private val gson: Gson = Gson()
) : ReadWriteProperty<Any, T> {

    private var value: T? = null

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return preferences.getString(name, null)?.let { json ->
            value ?: gson.fromJson(json, clazz).also { value = it }
        } ?: defaultValue.also { setValue(it) }
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) = setValue(value)

    private fun setValue(value: T) {
        this.value = value
        preferences.edit()
            .putString(name, gson.toJson(value))
            .apply()
    }
}

open class NullablePreference<T>(
    private val preferences: SharedPreferences,
    private val name: String,
    private val defaultValue: T?
) : ReadWriteProperty<Any, T?> {

    private var value: T? = null

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): T? {
        return value ?: preferences.get(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
        this.value = value
        preferences.put(name, value)
    }
}

class Preference<T>(
    preferences: SharedPreferences,
    name: String,
    defaultValue: T
) : NullablePreference<T>(preferences, name, defaultValue) {
    override fun getValue(thisRef: Any, property: KProperty<*>) =
        super.getValue(thisRef, property)!!
}

@Suppress("UNCHECKED_CAST")
fun <T> SharedPreferences.get(name: String, defaultValue: T? = null): T? {
    return when (defaultValue) {
        is Boolean -> getBoolean(name, defaultValue) as T
        is Float -> getFloat(name, defaultValue) as T
        is Long -> getLong(name, defaultValue) as T
        is Int -> getInt(name, defaultValue) as T
        is String -> getString(name, defaultValue) as T
        else -> defaultValue
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> SharedPreferences.put(name: String, value: T) {
    val editor = edit()

    when (value) {
        is Boolean -> editor.putBoolean(name, value)
        is Float -> editor.putFloat(name, value)
        is Long -> editor.putLong(name, value)
        is Int -> editor.putInt(name, value)
        is String -> editor.putString(name, value)
    }
    editor.apply()
}