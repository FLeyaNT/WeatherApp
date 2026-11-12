package com.example.weatherapp.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.di.AppContainer
import com.example.weatherapp.ui.favorites.FavoritesRoute
import com.example.weatherapp.ui.favorites.FavoritesViewModel
import com.example.weatherapp.ui.favorites.FavoritesViewModelFactory
import com.example.weatherapp.ui.favorites.components.FavoritesMessageDialog
import com.example.weatherapp.ui.search.SearchRoute
import com.example.weatherapp.ui.search.SearchViewModel
import com.example.weatherapp.ui.search.SearchViewModelFactory
import com.example.weatherapp.ui.weather.WeatherRoute
import com.example.weatherapp.ui.weather.WeatherViewModel
import com.example.weatherapp.ui.weather.WeatherViewModelFactory
import com.example.weatherapp.ui.settings.*
import com.example.weatherapp.ui.common.localizedText

private enum class AppScreen { WEATHER, SEARCH, FAVORITES, SETTINGS }

@Composable
fun WeatherApp(container: AppContainer, widgetOpenRequest: Int = 0) {
    SettingsProvider(container) { settingsViewModel ->
        WeatherNavigation(container, settingsViewModel, widgetOpenRequest)
    }
}

@Composable
private fun WeatherNavigation(container: AppContainer, settingsViewModel: SettingsViewModel, widgetOpenRequest: Int) {
    val weatherFactory = remember(container) {
        WeatherViewModelFactory(
            container.weatherRepository,
            container.widgetSync
        )
    }

    val searchFactory = remember(container) {
        SearchViewModelFactory(
            container.cityRepository,
            container.searchHistoryStorage,
            container.settingsStorage
        )
    }

    val weatherViewModel: WeatherViewModel = viewModel(
        factory = weatherFactory
    )

    val searchViewModel: SearchViewModel = viewModel(
        factory = searchFactory
    )

    val favoritesFactory = remember(container) {
        FavoritesViewModelFactory(container.favoritesStorage, container.weatherRepository)
    }
    val favoritesViewModel: FavoritesViewModel = viewModel(factory = favoritesFactory)
    val favorites by favoritesViewModel.state.collectAsStateWithLifecycle()

    AutoRefreshEffect(LocalAppSettings.current.refreshMinutes) {
        weatherViewModel.refreshAutomatically()
        favoritesViewModel.refresh()
    }

    var screen by rememberSaveable { mutableStateOf(AppScreen.WEATHER) }
    LaunchedEffect(widgetOpenRequest) {
        if (widgetOpenRequest > 0) {
            screen = AppScreen.WEATHER
            weatherViewModel.restoreSelectedCity()
        }
    }

    BackHandler(enabled = screen != AppScreen.WEATHER) {
        screen = AppScreen.WEATHER
    }

    when (screen) {
        AppScreen.SETTINGS -> SettingsRoute(settingsViewModel) { screen = AppScreen.WEATHER }
        AppScreen.SEARCH -> SearchRoute(
            viewModel = searchViewModel,
            onCitySelected = { city ->
                weatherViewModel.load(city)
                screen = AppScreen.WEATHER
            },
            onBack = {
                screen = AppScreen.WEATHER
            }
        )
        AppScreen.FAVORITES -> FavoritesRoute(
            viewModel = favoritesViewModel,
            onCitySelected = { city ->
                weatherViewModel.load(city)
                screen = AppScreen.WEATHER
            },
            onBack = { screen = AppScreen.WEATHER }
        )

        AppScreen.WEATHER -> WeatherRoute(
            viewModel = weatherViewModel,
            locationRepository =
                container.locationRepository,
            geocoder =
                container.reverseGeocoder,
            favoritesViewModel = favoritesViewModel,
            onSearch = {
                screen = AppScreen.SEARCH
            },
            onFavorites = { screen = AppScreen.FAVORITES },
            onSettings = { screen = AppScreen.SETTINGS }
        )
    }

    favorites.message?.let { message ->
        FavoritesMessageDialog(message.localizedText(), favoritesViewModel::dismissMessage)
    }
}
