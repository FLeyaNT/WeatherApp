package com.example.weatherapp.data.mapper

import com.example.weatherapp.data.remote.dto.DailyDto
import com.example.weatherapp.model.DailyForecast

fun DailyDto.toDailyForecasts(): List<DailyForecast> {
    return time.indices.mapNotNull { index ->
        val minimum =
            minTemperature.getOrNull(index)
                ?: return@mapNotNull null

        val maximum =
            maxTemperature.getOrNull(index)
                ?: return@mapNotNull null

        val sunriseTime =
            sunrise.getOrNull(index)
                ?: return@mapNotNull null

        val sunsetTime =
            sunset.getOrNull(index)
                ?: return@mapNotNull null

        val code =
            weatherCode.getOrNull(index)
                ?: return@mapNotNull null

        DailyForecast(
            timestamp = time[index],
            minTemperatureC = minimum,
            maxTemperatureC = maximum,
            precipitationProbability =
                precipitationProbability.getOrNull(index),
            sunrise = sunriseTime,
            sunset = sunsetTime,
            weatherCode = code
        )
    }
}