package com.example.weatherapp.util

import com.example.weatherapp.R

fun weatherDescription(code: Int): Int {
    return when (code) {
        0 -> R.string.condition_clear
        1 -> R.string.condition_mainly_clear
        2 -> R.string.condition_partly_cloudy
        3 -> R.string.condition_overcast

        45, 48 -> R.string.condition_fog

        51, 53, 55 -> R.string.condition_drizzle
        56, 57 -> R.string.condition_freezing_drizzle

        61, 63, 65 -> R.string.condition_rain
        66, 67 -> R.string.condition_freezing_rain

        71, 73, 75, 77 -> R.string.condition_snow

        80, 81, 82 -> R.string.condition_rain_showers
        85, 86 -> R.string.condition_snow_showers

        95, 97 -> R.string.condition_thunderstorm
        96, 99 -> R.string.condition_hail

        else -> R.string.condition_unknown
    }
}

fun weatherIcon(
    code: Int,
    isDay: Boolean = true
): String {
    return when (code) {
        0 -> if (isDay) "☀️" else "🌙"

        1, 2 -> "⛅"
        3 -> "☁️"

        45, 48 -> "🌫️"

        51, 53, 55, 56, 57,
        61, 63, 65, 66, 67,
        80, 81, 82 -> "🌧️"

        71, 73, 75, 77, 85, 86 -> "❄️"

        95, 96, 97, 99 -> "⛈️"

        else -> "🌡️"
    }
}