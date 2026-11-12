package com.example.weatherapp.data.remote.dto

data class CityDto(
    val id: Long,
    val name: String,
    val country: String?,
    val latitude: Double,
    val longitude: Double
)
