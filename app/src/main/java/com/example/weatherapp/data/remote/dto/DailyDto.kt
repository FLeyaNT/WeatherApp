package com.example.weatherapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DailyDto(
    val time: List<Long>,

    @SerializedName("temperature_2m_min")
    val minTemperature: List<Double>,

    @SerializedName("temperature_2m_max")
    val maxTemperature: List<Double>,

    @SerializedName("precipitation_probability_max")
    val precipitationProbability: List<Int?>,

    val sunrise: List<Long>,

    val sunset: List<Long>,

    @SerializedName("weather_code")
    val weatherCode: List<Int>
)
