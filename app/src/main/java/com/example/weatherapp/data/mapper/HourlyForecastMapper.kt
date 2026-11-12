package com.example.weatherapp.data.mapper

import com.example.weatherapp.data.remote.dto.HourlyDto
import com.example.weatherapp.model.HourlyForecast

fun HourlyDto.toHourlyForecasts(): List<HourlyForecast> {
    return time.indices.mapNotNull { index ->
        val temperatureValue =
            temperature.getOrNull(index)
                ?: return@mapNotNull null

        val code =
            weatherCode.getOrNull(index)
                ?: return@mapNotNull null

        HourlyForecast(
            timestamp = time[index],
            temperatureC = temperatureValue,
            precipitationProbability =
                precipitationProbability.getOrNull(index),
            precipitationMm =
                precipitation.getOrNull(index),
            weatherCode = code
        )
    }
}