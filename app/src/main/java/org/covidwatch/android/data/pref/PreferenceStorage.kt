package org.covidwatch.android.data.pref

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import org.covidwatch.android.data.*
import java.time.Instant
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Storage for app and user preferences.
 */
interface PreferenceStorage {
    var lastFetchDate: Long
    var onboardingFinished: Boolean
    var showOnboardingHomeAnimation: Boolean

    var lastCheckedForExposures: Instant
    val observableLastCheckedForExposures: LiveData<Instant>

    var riskMetrics: RiskMetrics?
    val observableRiskMetrics: LiveData<RiskMetrics?>

    var regions: Regions
    val observableRegions: LiveData<Regions>
    val region: Region
    val observableRegion: LiveData<Region>
    var selectedRegion: Int

    val riskModelConfiguration: RiskModelConfiguration
    val exposureConfiguration: CovidExposureConfiguration

    /**
     * Internal state version for the migration purposes
     */
    val version: Int
}

class SharedPreferenceStorage(context: Context) : PreferenceStorage {
    private val gson: Gson

    private val prefs = context.applicationContext.getSharedPreferences(NAME, MODE_PRIVATE)
    private val _lastCheckedForExposures = MutableLiveData<Instant>()
    private val _regions = MutableLiveData<Regions>()
    private val _riskMetrics = MutableLiveData<RiskMetrics?>()
    private val _region = MutableLiveData<Region>()

    private val changeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            LAST_CHECKED_FOR_EXPOSURES -> _lastCheckedForExposures.value = lastCheckedForExposures
            REGIONS -> {
                _regions.value = regions
                _region.value = region
            }
            SELECTED_REGION -> {
                _region.value = region
            }
        }
    }

    init {
        prefs.registerOnSharedPreferenceChangeListener(changeListener)
        gson = gsonWithInstantAdapter()
    }

    override val version: Int by Preference(prefs, "", -1)

    override var lastFetchDate by Preference(prefs, LAST_FETCH_DATE, 0L)

    override var onboardingFinished by Preference(prefs, ONBOARDING_FINISHED, false)

    override var showOnboardingHomeAnimation by Preference(
        prefs,
        SHOW_ONBOARDING_HOME_ANIMATION,
        true
    )

    override var lastCheckedForExposures: Instant by ObjectPreference(
        prefs,
        LAST_CHECKED_FOR_EXPOSURES,
        Instant.now(),
        Instant::class.java,
        gson = gson
    )

    override val observableLastCheckedForExposures: LiveData<Instant>
        get() = _lastCheckedForExposures.also { it.value = lastCheckedForExposures }

    override var riskMetrics: RiskMetrics? by NullableObjectPreference(
        prefs,
        RISK_METRICS,
        RiskMetrics::class.java,
        gson = gson
    )

    override val observableRiskMetrics: LiveData<RiskMetrics?>
        get() = _riskMetrics.also { it.value = riskMetrics }

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

    override val riskModelConfiguration: RiskModelConfiguration
        get() = region.riskModelConfiguration

    override var selectedRegion by Preference(prefs, SELECTED_REGION, 0)

    override val observableRegion: LiveData<Region>
        get() = _region.also { it.value = region }

    override val exposureConfiguration: CovidExposureConfiguration
        get() = region.exposureConfiguration.asCovidExposureConfiguration()

    companion object {
        private const val NAME = "ag_minimal_prefs"
        private const val LAST_FETCH_DATE = "last_fetch_date"
        private const val LAST_CHECKED_FOR_EXPOSURES = "last_checked_for_exposures"
        private const val RISK_METRICS = "risk_metrics"
        private const val REGIONS = "regions"
        private const val SELECTED_REGION = "selected_region"
        private const val ONBOARDING_FINISHED = "onboarding_finished"
        private const val SHOW_ONBOARDING_HOME_ANIMATION = "show_onboarding_home_animation"
    }
}

open class NullableObjectPreference<T>(
    private val preferences: SharedPreferences,
    private val name: String,
    private val clazz: Class<T>,
    private val defaultValue: T? = null,
    private val gson: Gson = Gson()
) : ReadWriteProperty<Any, T?> {

    private var value: T? = null

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): T? {
        return preferences.getString(name, null)?.let { json ->
            value ?: gson.fromJson(json, clazz).also { value = it }
        } ?: defaultValue?.also { setValue(it) }
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) = setValue(value)

    private fun setValue(value: T?) {
        this.value = value
        preferences.edit()
            .putString(name, gson.toJson(value))
            .apply()
    }
}

class ObjectPreference<T>(
    preferences: SharedPreferences,
    name: String,
    defaultValue: T,
    clazz: Class<T>,
    gson: Gson = Gson()
) : NullableObjectPreference<T>(preferences, name, clazz, defaultValue, gson) {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>) =
        super.getValue(thisRef, property)!!
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