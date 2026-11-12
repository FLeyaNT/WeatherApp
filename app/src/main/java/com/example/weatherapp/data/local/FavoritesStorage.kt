package com.example.weatherapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.weatherapp.model.City
import com.example.weatherapp.model.favoriteId
import com.google.gson.Gson
import com.google.gson.JsonParseException
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

enum class FavoriteChange { ADDED, REMOVED, LIMIT_REACHED }

class FavoritesStorage(private val dataStore: DataStore<Preferences>) {
    private val gson = Gson()
    private val citiesKey = stringPreferencesKey("cities")

    val cities = dataStore.data
        .map { decode(it[citiesKey]) }
        .distinctUntilChanged()

    suspend fun toggleCity(city: City): FavoriteChange {
        val savedCity = city.copy(id = city.favoriteId())
        var change = FavoriteChange.ADDED

        // Проверяем лимит и записываем список в одной транзакции.
        dataStore.edit { preferences ->
            val current = decode(preferences[citiesKey])
            when {
                current.any { it.id == savedCity.id } -> {
                    change = FavoriteChange.REMOVED
                    preferences[citiesKey] = gson.toJson(
                        removeFavorite(current, savedCity.id)
                    )
                }
                current.size >= MAX_CITIES -> {
                    change = FavoriteChange.LIMIT_REACHED
                }
                else -> {
                    preferences[citiesKey] = gson.toJson(
                        addFavorite(current, savedCity)
                    )
                }
            }
        }
        return change
    }

    suspend fun removeCity(cityId: String) {
        dataStore.edit { preferences ->
            preferences[citiesKey] = gson.toJson(
                removeFavorite(decode(preferences[citiesKey]), cityId)
            )
        }
    }

    private fun decode(json: String?): List<City> {
        if (json == null) return emptyList()
        return try {
            gson.fromJson(json, Array<City>::class.java)
                ?.toList().orEmpty()
        } catch (_: JsonParseException) {
            emptyList()
        }
    }

    companion object {
        const val MAX_CITIES = 10
    }
}
