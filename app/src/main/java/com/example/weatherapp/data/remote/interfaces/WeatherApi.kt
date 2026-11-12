package com.example.weatherapp.data.remote.interfaces

import com.example.weatherapp.data.remote.dto.ForecastDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude")
        latitude: Double,

        @Query("longitude")
        longitude: Double,

        @Query("current")
        current: String =
            "temperature_2m,relative_humidity_2m," +
                    "apparent_temperature,is_day,weather_code," +
                    "surface_pressure,wind_speed_10m",

        @Query("hourly")
        hourly: String =
            "temperature_2m,precipitation_probability," +
                    "precipitation,weather_code,visibility",

        @Query("daily")
        daily: String =
            "weather_code,temperature_2m_max," +
                    "temperature_2m_min,sunrise,sunset," +
                    "precipitation_probability_max",

        @Query("forecast_days")
        days: Int = 7,

        @Query("wind_speed_unit")
        windUnit: String = "ms",

        @Query("timezone")
        timezone: String = "auto",

        @Query("timeformat")
        timeFormat: String = "unixtime"
    ): ForecastDto
}