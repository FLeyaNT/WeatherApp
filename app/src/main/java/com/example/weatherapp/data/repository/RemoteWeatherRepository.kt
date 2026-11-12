package com.example.weatherapp.data.repository

import com.example.weatherapp.data.mapper.toWeather
import com.example.weatherapp.data.remote.interfaces.WeatherApi
import com.example.weatherapp.model.City
import com.example.weatherapp.model.Weather

class RemoteWeatherRepository(
    private val api: WeatherApi
) : WeatherRepository {

    override suspend fun loadWeather(city: City): Weather {
        val response = api.getForecast(
            latitude = city.latitude,
            longitude = city.longitude
        )

        return response.toWeather(city)
    }
}