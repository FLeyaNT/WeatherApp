package com.example.weatherapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class HourlyDto(
    val time: List<Long>,

    @SerializedName("temperature_2m")
    val temperature: List<Double>,

    @SerializedName("precipitation_probability")
    val precipitationProbability: List<Int?>,

    val precipitation: List<Double?>,

    val visibility: List<Double?>,

    @SerializedName("weather_code")
    val weatherCode: List<Int>
)
