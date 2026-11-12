package com.example.weatherapp.ui.weather

import com.example.weatherapp.ui.common.UiMessage

import com.example.weatherapp.model.Weather

sealed interface WeatherUiState {
    data object Initial : WeatherUiState

    data object  Loading : WeatherUiState

    data class Success(
        val weather: Weather
    ) : WeatherUiState

    data class Error(
        val message: UiMessage
    ) : WeatherUiState
}