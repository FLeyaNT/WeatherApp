package com.example.weatherapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.JsonParseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class SearchHistoryStorage(
    private val dataStore: DataStore<Preferences>
) {
    private val gson = Gson()
    private val queriesKey = stringPreferencesKey("queries")

    val history: Flow<List<String>> = dataStore.data
        .map { preferences -> decode(preferences[queriesKey]) }
        .distinctUntilChanged()

    suspend fun addQuery(query: String) {
        if (query.isBlank()) return

        dataStore.edit { preferences ->
            val current = decode(preferences[queriesKey])
            preferences[queriesKey] = gson.toJson(
                updateHistory(current, query)
            )
        }
    }

    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.remove(queriesKey)
        }
    }

    private fun decode(json: String?): List<String> {
        if (json == null) return emptyList()

        return try {
            gson.fromJson(json, Array<String>::class.java)
                ?.toList()
                .orEmpty()
        } catch (_: JsonParseException) {
            emptyList()
        }
    }
}
