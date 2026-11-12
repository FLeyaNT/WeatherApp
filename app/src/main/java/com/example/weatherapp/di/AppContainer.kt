package com.example.weatherapp.di

import android.content.Context
import com.example.weatherapp.data.local.SettingsStorage
import com.example.weatherapp.data.local.settingsDataStore
import com.example.weatherapp.data.local.FavoritesStorage
import com.example.weatherapp.data.local.favoritesDataStore
import com.example.weatherapp.data.local.SearchHistoryStorage
import com.example.weatherapp.data.local.searchHistoryDataStore
import com.example.weatherapp.data.location.LocationRepository
import com.example.weatherapp.data.location.ReverseGeocoder
import com.example.weatherapp.data.remote.clients.ApiClient
import com.example.weatherapp.data.repository.CityRepository
import com.example.weatherapp.data.repository.RemoteWeatherRepository
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.data.local.WeatherWidgetStorage
import com.example.weatherapp.data.local.weatherWidgetDataStore
import com.example.weatherapp.widget.WeatherWidgetSync

class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    val settingsStorage = SettingsStorage(appContext.settingsDataStore)
    val widgetStorage = WeatherWidgetStorage(appContext.weatherWidgetDataStore)
    val widgetSync = WeatherWidgetSync(appContext, widgetStorage)

    val weatherRepository: WeatherRepository =
        RemoteWeatherRepository(
            api = ApiClient.weatherApi
        )

    val cityRepository = CityRepository(
        api = ApiClient.geocodingApi
    )

    val searchHistoryStorage = SearchHistoryStorage(
        appContext.searchHistoryDataStore
    )

    val favoritesStorage = FavoritesStorage(appContext.favoritesDataStore)

    val locationRepository =
        LocationRepository(appContext)

    val reverseGeocoder =
        ReverseGeocoder(appContext)
}
