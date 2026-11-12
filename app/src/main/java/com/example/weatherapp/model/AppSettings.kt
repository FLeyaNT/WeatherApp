package com.example.weatherapp.model

enum class TemperatureUnit { CELSIUS, FAHRENHEIT }
enum class WindUnit { MS, KMH, MPH }
enum class PressureUnit { MMHG, HPA, INHG }
enum class AppLanguage(val tag: String) { RUSSIAN("ru"), ENGLISH("en") }
enum class GpsAccuracy { HIGH, BALANCED, LOW_POWER }

data class AppSettings(
    val temperature: TemperatureUnit = TemperatureUnit.CELSIUS,
    val wind: WindUnit = WindUnit.MS,
    val pressure: PressureUnit = PressureUnit.HPA,
    val language: AppLanguage = AppLanguage.RUSSIAN,
    val refreshMinutes: Int = 30,
    val gpsAccuracy: GpsAccuracy = GpsAccuracy.BALANCED,
    val gpsTimeoutSeconds: Int = 20
) {
    companion object {
        val refreshOptions = listOf(15, 30, 60, 120)
        val timeoutOptions = listOf(10, 20, 30, 60)
    }
}
