package com.example.weatherapp.widget

import android.content.Context
import android.content.res.Configuration
import com.example.weatherapp.R
import com.example.weatherapp.model.AppSettings
import com.example.weatherapp.model.City
import com.example.weatherapp.model.DefaultCities
import com.example.weatherapp.model.TemperatureUnit
import com.example.weatherapp.model.WindUnit
import com.example.weatherapp.util.temperatureValue
import com.example.weatherapp.util.windValue
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

internal fun Context.widgetContext(settings: AppSettings): Context {
    val configuration = Configuration(resources.configuration)
    configuration.setLocale(Locale.forLanguageTag(settings.language.tag))
    return createConfigurationContext(configuration)
}

internal fun widgetCityName(context: Context, city: City): String = when {
    city.id == DefaultCities.Moscow.id -> context.getString(R.string.moscow)
    city.name == "Моя локация" || city.name == "My location" -> context.getString(R.string.my_location)
    else -> city.name
}

internal fun widgetTemperature(celsius: Double, settings: AppSettings): String {
    val suffix = if (settings.temperature == TemperatureUnit.CELSIUS) "°C" else "°F"
    return "${temperatureValue(celsius, settings.temperature).roundToInt()} $suffix"
}

internal fun widgetWind(context: Context, speed: Double, settings: AppSettings): String {
    val unit = context.getString(when (settings.wind) {
        WindUnit.MS -> R.string.unit_ms
        WindUnit.KMH -> R.string.unit_kmh
        WindUnit.MPH -> R.string.unit_mph
    })
    return String.format(Locale.forLanguageTag(settings.language.tag), "%.1f %s", windValue(speed, settings.wind), unit)
}

internal fun widgetDataTime(timestamp: Long, timezone: String, settings: AppSettings): String {
    val zone = runCatching { ZoneId.of(timezone) }.getOrDefault(ZoneId.of("UTC"))
    return Instant.ofEpochSecond(timestamp).atZone(zone)
        .format(DateTimeFormatter.ofPattern("dd.MM HH:mm", Locale.forLanguageTag(settings.language.tag)))
}
