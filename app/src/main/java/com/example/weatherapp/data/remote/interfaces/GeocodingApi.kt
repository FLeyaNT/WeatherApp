package com.example.weatherapp.data.remote.interfaces

import com.example.weatherapp.data.remote.dto.CitySearchDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {
    @GET("v1/search")
    suspend fun searchCities(
        @Query("name") query: String,
        @Query("count") count: Int = 10,
        @Query("language") language: String = "ru"
    ): CitySearchDto
}