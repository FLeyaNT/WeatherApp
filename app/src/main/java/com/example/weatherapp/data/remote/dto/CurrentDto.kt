package com.example.weatherapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CurrentDto(
    val time: Long,

    @SerializedName("temperature_2m")
    val temperature: Double,

    @SerializedName("relative_humidity_2m")
    val humidity: Int,

    @SerializedName("apparent_temperature")
    val feelsLike: Double,

    @SerializedName("surface_pressure")
    val pressure: Double,

    @SerializedName("wind_speed_10m")
    val windSpeed: Double,

    @SerializedName("weather_code")
    val weatherCode: Int,

    @SerializedName("is_day")
    val isDay: Int
)
