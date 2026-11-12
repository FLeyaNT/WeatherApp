package com.example.weatherapp.data.local

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

// One DataStore instance per file, shared by all consumers in the app.
val Context.searchHistoryDataStore by preferencesDataStore(
    name = "search_history"
)
