package com.example.weatherapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.example.weatherapp.model.*
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class SettingsStorage(private val dataStore: DataStore<Preferences>) {
    private val temperature = stringPreferencesKey("temperature")
    private val wind = stringPreferencesKey("wind")
    private val pressure = stringPreferencesKey("pressure")
    private val language = stringPreferencesKey("language")
    private val refresh = intPreferencesKey("refresh_minutes")
    private val accuracy = stringPreferencesKey("gps_accuracy")
    private val timeout = intPreferencesKey("gps_timeout_seconds")

    val settings = dataStore.data.map(::decode).distinctUntilChanged()

    suspend fun update(change: (AppSettings) -> AppSettings) {
        dataStore.edit { preferences ->
            val value = change(decode(preferences))
            require(value.refreshMinutes in AppSettings.refreshOptions)
            require(value.gpsTimeoutSeconds in AppSettings.timeoutOptions)
            preferences[temperature] = value.temperature.name
            preferences[wind] = value.wind.name
            preferences[pressure] = value.pressure.name
            preferences[language] = value.language.name
            preferences[refresh] = value.refreshMinutes
            preferences[accuracy] = value.gpsAccuracy.name
            preferences[timeout] = value.gpsTimeoutSeconds
        }
    }

    private fun decode(preferences: Preferences): AppSettings {
        val defaults = AppSettings()
        return AppSettings(
            temperature = enumValue(preferences[temperature], defaults.temperature),
            wind = enumValue(preferences[wind], defaults.wind),
            pressure = enumValue(preferences[pressure], defaults.pressure),
            language = enumValue(preferences[language], defaults.language),
            refreshMinutes = preferences[refresh]?.takeIf { it in AppSettings.refreshOptions }
                ?: defaults.refreshMinutes,
            gpsAccuracy = enumValue(preferences[accuracy], defaults.gpsAccuracy),
            gpsTimeoutSeconds = preferences[timeout]?.takeIf { it in AppSettings.timeoutOptions }
                ?: defaults.gpsTimeoutSeconds
        )
    }

    private inline fun <reified T : Enum<T>> enumValue(name: String?, default: T): T =
        enumValues<T>().firstOrNull { it.name == name } ?: default
}
