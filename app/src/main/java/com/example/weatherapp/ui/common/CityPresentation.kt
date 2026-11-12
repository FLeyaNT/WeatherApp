package com.example.weatherapp.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.weatherapp.R
import com.example.weatherapp.model.City
import com.example.weatherapp.model.DefaultCities

@Composable
fun cityDisplayName(city: City): String = when {
    city.id == DefaultCities.Moscow.id -> stringResource(R.string.moscow)
    city.name == "Моя локация" || city.name == "My location" -> stringResource(R.string.my_location)
    else -> city.name
}

@Composable
fun cityCountry(city: City): String = if (city.id == DefaultCities.Moscow.id) {
    stringResource(R.string.russia)
} else {
    city.country
}
