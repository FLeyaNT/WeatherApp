package com.example.weatherapp.data.local

import com.example.weatherapp.model.City

fun updateHistory(
    current: List<String>,
    newQuery: String
): List<String> {
    val query = newQuery.trim()

    if (query.isBlank()) return current

    return (
            listOf(query) +
                    current.filterNot {
                        it.equals(query, ignoreCase = true)
                    }
            ).take(5)
}

fun addFavorite(
    current: List<City>,
    city: City
): List<City> {
    if (current.any { it.id == city.id }) {
        return current
    }

    require(current.size < 10) {
        "Можно сохранить не более 10 городов"
    }

    return current + city
}

fun removeFavorite(
    current: List<City>,
    cityId: String
): List<City> {
    return current.filterNot { it.id == cityId }
}