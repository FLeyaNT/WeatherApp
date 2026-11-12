package com.example.weatherapp.data.local

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.favoritesDataStore by preferencesDataStore(name = "favorite_cities")
