package com.example.weatherapp.model

data class DailyForecast(
    val timestamp: Long,
    val minTemperatureC: Double,
    val maxTemperatureC: Double,
    val precipitationProbability: Int?,
    val sunrise: Long,
    val sunset: Long,
    val weatherCode: Int
)
