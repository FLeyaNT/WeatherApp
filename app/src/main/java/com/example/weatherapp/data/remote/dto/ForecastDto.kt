package com.example.weatherapp.data.remote.dto

data class ForecastDto(
    val timezone: String,
    val current: CurrentDto,
    val hourly: HourlyDto,
    val daily: DailyDto
)
