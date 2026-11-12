package com.example.weatherapp.model

data class HourlyForecast(
    val timestamp: Long,
    val temperatureC: Double,
    val precipitationProbability: Int?,
    val precipitationMm: Double?,
    val weatherCode: Int
)
