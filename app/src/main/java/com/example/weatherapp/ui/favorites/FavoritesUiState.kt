package com.example.weatherapp.ui.favorites

import com.example.weatherapp.ui.common.UiMessage

import com.example.weatherapp.model.City
import com.example.weatherapp.model.CurrentWeather
import com.example.weatherapp.model.favoriteId

data class FavoriteCityUiState(
    val city: City,
    val current: CurrentWeather? = null,
    val timezone: String = "UTC",
    val loading: Boolean = false,
    val error: UiMessage? = null
)

data class FavoritesUiState(
    val cities: List<FavoriteCityUiState> = emptyList(),
    val reading: Boolean = true,
    val saving: Boolean = false,
    val message: UiMessage? = null
) {
    fun contains(city: City): Boolean =
        cities.any { it.city.id == city.favoriteId() }
}
