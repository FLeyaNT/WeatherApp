package com.example.weatherapp.util

import com.example.weatherapp.model.*

fun temperatureValue(celsius: Double, unit: TemperatureUnit): Double = when (unit) {
    TemperatureUnit.CELSIUS -> celsius
    TemperatureUnit.FAHRENHEIT -> celsius * 9.0 / 5.0 + 32.0
}

fun windValue(metersPerSecond: Double, unit: WindUnit): Double = when (unit) {
    WindUnit.MS -> metersPerSecond
    WindUnit.KMH -> metersPerSecond * 3.6
    WindUnit.MPH -> metersPerSecond * 2.2369362921
}

fun pressureValue(hpa: Double, unit: PressureUnit): Double = when (unit) {
    PressureUnit.MMHG -> hpa * 0.750061683
    PressureUnit.HPA -> hpa
    PressureUnit.INHG -> hpa * 0.0295299831
}
