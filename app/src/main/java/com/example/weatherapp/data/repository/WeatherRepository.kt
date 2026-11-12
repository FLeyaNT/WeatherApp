package com.example.weatherapp.data.repository

import com.example.weatherapp.model.City
import com.example.weatherapp.model.Weather

interface WeatherRepository {
    suspend fun loadWeather(city: City): Weather
}