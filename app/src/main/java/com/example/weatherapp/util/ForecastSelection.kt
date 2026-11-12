package com.example.weatherapp.util

import com.example.weatherapp.model.HourlyForecast
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

fun next24Hours(
    forecast: List<HourlyForecast>,
    nowSeconds: Long,
    timezone: String = "UTC"
): List<HourlyForecast> {
    val currentHour = Instant.ofEpochSecond(nowSeconds)
        .atZone(ZoneId.of(timezone))
        .truncatedTo(ChronoUnit.HOURS)
        .toEpochSecond()

    return forecast
        .asSequence()
        .filter { it.timestamp >= currentHour }
        .sortedBy { it.timestamp }
        .take(24)
        .toList()
}