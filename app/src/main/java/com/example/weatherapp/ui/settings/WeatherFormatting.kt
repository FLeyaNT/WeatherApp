package com.example.weatherapp.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.weatherapp.R
import com.example.weatherapp.model.*
import com.example.weatherapp.util.*
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun temperatureText(celsius: Double): String {
    val unit = LocalAppSettings.current.temperature
    val suffix = if (unit == TemperatureUnit.CELSIUS) "°C" else "°F"
    return "${temperatureValue(celsius, unit).roundToInt()} $suffix"
}

@Composable
fun temperatureRangeText(minimum: Double, maximum: Double): String {
    val unit = LocalAppSettings.current.temperature
    return "${temperatureValue(minimum, unit).roundToInt()}° / ${temperatureValue(maximum, unit).roundToInt()}°"
}

@Composable
fun windText(metersPerSecond: Double): String {
    val settings = LocalAppSettings.current
    val label = stringResource(when (settings.wind) {
        WindUnit.MS -> R.string.unit_ms
        WindUnit.KMH -> R.string.unit_kmh
        WindUnit.MPH -> R.string.unit_mph
    })
    return String.format(Locale.forLanguageTag(settings.language.tag), "%.1f %s",
        windValue(metersPerSecond, settings.wind), label)
}

@Composable
fun pressureText(hpa: Double): String {
    val settings = LocalAppSettings.current
    val label = stringResource(when (settings.pressure) {
        PressureUnit.MMHG -> R.string.unit_mmhg
        PressureUnit.HPA -> R.string.unit_hpa
        PressureUnit.INHG -> R.string.unit_inhg
    })
    return String.format(Locale.forLanguageTag(settings.language.tag), "%.1f %s",
        pressureValue(hpa, settings.pressure), label)
}

@Composable
fun visibilityText(meters: Double?): String {
    val locale = Locale.forLanguageTag(LocalAppSettings.current.language.tag)
    return meters?.let { String.format(locale, "%.1f %s", it / 1000.0, stringResource(R.string.unit_km)) }
        ?: stringResource(R.string.no_data)
}
