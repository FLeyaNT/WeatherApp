package com.example.weatherapp.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun formatCityTime(
    timestamp: Long,
    timezone: String
): String {
    return Instant.ofEpochSecond(timestamp)
        .atZone(ZoneId.of(timezone))
        .format(DateTimeFormatter.ofPattern("HH:mm"))
}