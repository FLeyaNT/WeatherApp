package com.example.weatherapp.model

data class CurrentWeather(
    val temperatureC: Double,
    val feelsLikeC: Double,
    val humidityPercent: Int,
    val windSpeedMs: Double,
    val pressureHpa: Double,
    val visibilityMeters: Double?,
    val weatherCode: Int,
    val isDay: Boolean,
    val observedAt: Long
)
