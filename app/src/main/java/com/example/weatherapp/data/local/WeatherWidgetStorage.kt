package com.example.weatherapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.weatherapp.model.City
import com.example.weatherapp.model.CurrentWeather
import com.example.weatherapp.model.DefaultCities
import com.example.weatherapp.model.Weather
import com.google.gson.Gson
import com.google.gson.JsonParseException
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

val Context.weatherWidgetDataStore by preferencesDataStore(name = "weather_widget")

data class WidgetSnapshot(val current: CurrentWeather, val timezone: String)

data class WeatherWidgetState(
    val city: City = DefaultCities.Moscow,
    val snapshot: WidgetSnapshot? = null,
    val refreshFailed: Boolean = false
)

data class WidgetRefreshRequest(val city: City, val generation: Long)

/** Сохраняем город и последнюю погоду, чтобы виджет работал после закрытия приложения. */
class WeatherWidgetStorage(private val dataStore: DataStore<Preferences>) {
    private val gson = Gson()
    private val cityKey = stringPreferencesKey("city")
    private val snapshotKey = stringPreferencesKey("snapshot")
    private val errorKey = booleanPreferencesKey("refresh_failed")
    private val generationKey = longPreferencesKey("generation")

    val state = dataStore.data.map(::decode).distinctUntilChanged()

    suspend fun selectCity(city: City) {
        dataStore.edit { preferences ->
            if (decode(preferences).city != city) {
                preferences[cityKey] = gson.toJson(city)
                preferences.remove(snapshotKey)
                preferences[errorKey] = false
                nextGeneration(preferences)
            }
        }
    }

    suspend fun saveWeather(weather: Weather) {
        dataStore.edit { preferences ->
            if (decode(preferences).city == weather.city) {
                writeWeather(preferences, weather)
                nextGeneration(preferences)
            }
        }
    }

    suspend fun markFailed(city: City) {
        dataStore.edit { preferences ->
            if (decode(preferences).city == city) {
                preferences[errorKey] = true
                nextGeneration(preferences)
            }
        }
    }

    suspend fun beginRefresh(): WidgetRefreshRequest {
        var request: WidgetRefreshRequest? = null
        dataStore.edit { preferences ->
            request = WidgetRefreshRequest(decode(preferences).city, nextGeneration(preferences))
        }
        return checkNotNull(request)
    }

    suspend fun finishRefresh(request: WidgetRefreshRequest, weather: Weather?) {
        dataStore.edit { preferences ->
            // Запоздавший ответ не должен заменить новый город или более свежую погоду.
            if (preferences[generationKey] == request.generation &&
                decode(preferences).city == request.city
            ) {
                if (weather != null && weather.city == request.city) {
                    writeWeather(preferences, weather)
                } else {
                    preferences[errorKey] = true
                }
            }
        }
    }

    private fun writeWeather(preferences: MutablePreferences, weather: Weather) {
        val cached = readJson(preferences[snapshotKey], WidgetSnapshot::class.java)
        if (cached == null || weather.current.observedAt >= cached.current.observedAt) {
            preferences[snapshotKey] = gson.toJson(WidgetSnapshot(weather.current, weather.timezone))
        }
        preferences[errorKey] = false
    }

    private fun nextGeneration(preferences: MutablePreferences): Long {
        val generation = (preferences[generationKey] ?: 0L) + 1
        preferences[generationKey] = generation
        return generation
    }

    private fun decode(preferences: Preferences) = WeatherWidgetState(
        city = readJson(preferences[cityKey], City::class.java) ?: DefaultCities.Moscow,
        snapshot = readJson(preferences[snapshotKey], WidgetSnapshot::class.java),
        refreshFailed = preferences[errorKey] ?: false
    )

    private fun <T> readJson(json: String?, type: Class<T>): T? = try {
        json?.let { gson.fromJson(it, type) }
    } catch (_: JsonParseException) {
        null
    }
}
