package com.example.weatherapp.data.mapper

import com.example.weatherapp.data.remote.dto.ForecastDto
import com.example.weatherapp.model.City
import com.example.weatherapp.model.CurrentWeather
import com.example.weatherapp.model.Weather
import kotlin.math.abs

fun ForecastDto.toWeather(city: City): Weather {
    val hourlyForecast = hourly.toHourlyForecasts()
    val dailyForecast = daily.toDailyForecasts()

    check(hourlyForecast.isNotEmpty()) {
        "Hourly forecast is missing"
    }

    check(dailyForecast.isNotEmpty()) {
        "Daily forecast is missing"
    }

    val visibilityIndex = hourly.time.indices.minByOrNull {
        abs(hourly.time[it] - current.time)
    }

    val visibilityValue = visibilityIndex?.let {
        hourly.visibility.getOrNull(it)
    }

    return Weather(
        city = city,
        timezone = timezone,
        current = CurrentWeather(
            temperatureC = current.temperature,
            feelsLikeC = current.feelsLike,
            humidityPercent = current.humidity,
            windSpeedMs = current.windSpeed,
            pressureHpa = current.pressure,
            visibilityMeters = visibilityValue,
            weatherCode = current.weatherCode,
            isDay = current.isDay == 1,
            observedAt = current.time
        ),
        hourly = hourlyForecast,
        daily = dailyForecast
    )
}