package com.example.weatherapp.util

import com.example.weatherapp.R

data class PrecipitationCondition(val icon: String, val description: Int)

fun precipitationCondition(weatherCode: Int): PrecipitationCondition {
    return when (weatherCode) {
        51, 53, 55 -> PrecipitationCondition("🌦️", R.string.condition_drizzle)
        56, 57 -> PrecipitationCondition("🌧️", R.string.condition_freezing_drizzle)
        61, 63, 65, 80, 81, 82 -> PrecipitationCondition("🌧️", R.string.condition_rain)
        66, 67 -> PrecipitationCondition("🧊", R.string.condition_freezing_rain)
        71, 73, 75, 77, 85, 86 -> PrecipitationCondition("❄️", R.string.condition_snow)
        95, 97 -> PrecipitationCondition("⛈️", R.string.condition_thunderstorm)
        96, 99 -> PrecipitationCondition("⛈️", R.string.condition_hail)
        // Вероятность бывает положительной и при коде без определённого типа осадков.
        else -> PrecipitationCondition("💧", R.string.condition_precipitation)
    }
}
