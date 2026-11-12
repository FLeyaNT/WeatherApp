package com.example.weatherapp.data.location

import com.example.weatherapp.model.GpsAccuracy

fun locationProviders(accuracy: GpsAccuracy, preciseAllowed: Boolean): List<String> =
    when (accuracy) {
        GpsAccuracy.HIGH -> if (preciseAllowed) listOf("gps") else emptyList()
        GpsAccuracy.BALANCED -> if (preciseAllowed) listOf("network", "gps") else listOf("network")
        GpsAccuracy.LOW_POWER -> listOf("network")
    }
