package com.example.weatherapp.data.repository

import com.example.weatherapp.data.remote.interfaces.GeocodingApi
import com.example.weatherapp.model.City

class CityRepository(
    private val api: GeocodingApi
) {

    suspend fun search(query: String, language: String = "ru"): List<City> {
        return api.searchCities(
            query = query,
            language = language
        )
            .results
            .orEmpty()
            .map { dto ->
                City(
                    id = dto.id.toString(),
                    name = dto.name,
                    country = dto.country.orEmpty(),
                    latitude = dto.latitude,
                    longitude = dto.longitude
                )
            }
    }
}
