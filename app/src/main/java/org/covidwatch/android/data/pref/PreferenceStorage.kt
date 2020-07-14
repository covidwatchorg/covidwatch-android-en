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
import org.covidwatch.android.data.DefaultRegions
import org.covidwatch.android.data.Region
import org.covidwatch.android.data.Regions
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Storage for app and user preferences.
 */
interface PreferenceStorage {
    var lastFetchDate: Long
    var onboardingFinished: Boolean
    var showOnboardingHomeAnimation: Boolean
    var exposureSummary: CovidExposureSummary
    val riskLevelValue: Float?
    val observableRiskLevelValue: LiveData<Float?>

    fun resetExposureSummary()

    var regions: Regions
    val observableRegions: LiveData<Regions>

    val region: Region
    var selectedRegion: Int
    val observableRegion: LiveData<Region>

    // TODO: 05.06.2020 Replace with our own class when the API is stable
    var exposureConfiguration: ExposureConfiguration
    val observableExposureSummary: LiveData<CovidExposureSummary>
}

class SharedPreferenceStorage(context: Context) : PreferenceStorage {
    private val prefs = context.applicationContext.getSharedPreferences(NAME, MODE_PRIVATE)
    private val _exposureSummary = MutableLiveData<CovidExposureSummary>()
    private val _regions = MutableLiveData<Regions>()
    private val _ristkLevelValue = MutableLiveData<Float>()
    private val _region = MutableLiveData<Region>()
    private val defaultExposureSummary = CovidExposureSummary(
        daySinceLastExposure = 0,
        matchedKeyCount = 0,
        maximumRiskScore = 0,
        attenuationDurationsInMinutes = intArrayOf(),
        summationRiskScore = 0
    )

    private val changeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            EXPOSURE_SUMMARY -> _exposureSummary.value = exposureSummary
            REGIONS -> {
                _regions.value = regions
                _region.value = region
            }
        }
    }

    init {
        prefs.registerOnSharedPreferenceChangeListener(changeListener)
    }

    override var lastFetchDate by Preference(prefs, LAST_FETCH_DATE, 0L)

    override var onboardingFinished by Preference(prefs, ONBOARDING_FINISHED, false)

    override var showOnboardingHomeAnimation by Preference(
        prefs,
        SHOW_ONBOARDING_HOME_ANIMATION,
        true
    )

    override var exposureSummary: CovidExposureSummary by ObjectPreference(
        prefs,
        EXPOSURE_SUMMARY,
        defaultExposureSummary,
        CovidExposureSummary::class.java
    )

    override var riskLevelValue: Float? by NullablePreference(
        prefs,
        RISK_LEVEL_VALUE,
        null
    )

    override val observableRiskLevelValue: LiveData<Float?>
        get() = _ristkLevelValue.also { it.value = riskLevelValue }

    override var regions: Regions by ObjectPreference(
        prefs,
        REGIONS,
        Regions(DefaultRegions.all),
        Regions::class.java
    )

    override val observableRegions: LiveData<Regions>
        get() = _regions.also { it.value = regions }

    override val region: Region
        get() = regions.regions[selectedRegion]

    override var selectedRegion by Preference(prefs, SELECTED_REGION, 0)

    override val observableRegion: LiveData<Region>
        get() = _region.also { it.value = region }

    override fun resetExposureSummary() {
        exposureSummary = defaultExposureSummary
    }

    override var exposureConfiguration: ExposureConfiguration by ObjectPreference(
        prefs,
        EXPOSURE_CONFIGURATION,
        ExposureConfiguration.ExposureConfigurationBuilder()
            .setMinimumRiskScore(1)
            .setDurationAtAttenuationThresholds(50, 58)
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
        private const val EXPOSURE_SUMMARY = "next_steps"
        private const val RISK_LEVEL_VALUE = "risk_level_value"
        private const val REGIONS = "regions"
        private const val SELECTED_REGION = "selected_region"
        private const val EXPOSURE_CONFIGURATION = "exposure_configuration"
        private const val ONBOARDING_FINISHED = "onboarding_finished"
        private const val SHOW_ONBOARDING_HOME_ANIMATION = "show_onboarding_home_animation"
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