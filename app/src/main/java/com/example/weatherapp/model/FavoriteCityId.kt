package com.example.weatherapp.model

// Избранная GPS-точка — фиксированное место, а не меняющаяся текущая локация.
fun City.favoriteId(): String = if (id == "gps") {
    "coordinates:$latitude:$longitude"
} else {
    id
}
