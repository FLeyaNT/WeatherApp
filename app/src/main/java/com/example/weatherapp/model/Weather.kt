package com.example.weatherapp.model

data class Weather(
    val city: City,
    val timezone: String,
    val current: CurrentWeather,
    val hourly: List<HourlyForecast>,
    val daily: List<DailyForecast>
)
