package com.example.weatherapp.ui.weather

import com.example.weatherapp.R
import androidx.compose.ui.res.stringResource

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapp.model.Weather
import com.example.weatherapp.ui.common.cityDisplayName
import com.example.weatherapp.ui.components.CurrentWeatherCard
import com.example.weatherapp.ui.components.DailyForecastRow
import com.example.weatherapp.ui.components.HourlyForecastRow
import com.example.weatherapp.ui.components.SunTimesCard
import com.example.weatherapp.util.formatCityTime
import com.example.weatherapp.util.next24Hours
import java.time.Instant
import java.time.ZoneId

@Composable
fun WeatherContent(
    weather: Weather,
    description: String,
    onLocate: () -> Unit,
    isFavorite: Boolean,
    savingFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
    locating: Boolean = false,
) {
    val hours = next24Hours(
        forecast = weather.hourly,
        nowSeconds = Instant.now().epochSecond,
        timezone = weather.timezone
    )

    val today = Instant.now()
        .atZone(ZoneId.of(weather.timezone))
        .toLocalDate()

    val todayForecast = weather.daily.firstOrNull {
        Instant.ofEpochSecond(it.timestamp)
            .atZone(ZoneId.of(weather.timezone))
            .toLocalDate() == today
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            CurrentWeatherCard(
                cityName = cityDisplayName(weather.city),
                isGpsLocation = weather.city.id == "gps",
                weather = weather.current,
                description = description,
                isFavorite = isFavorite,
                savingFavorite = savingFavorite,
                onToggleFavorite = onToggleFavorite
            )

        }

        item {
            OutlinedButton(
                onClick = onLocate,
                enabled = !locating
            ) {
                Text(
                    if (locating) {
                        stringResource(R.string.locating)
                    } else {
                        stringResource(R.string.locate)
                    }
                )
            }
        }

        todayForecast?.let { forecast ->
            item {
                SunTimesCard(
                    forecast = forecast,
                    timezone = weather.timezone
                )
            }
        }

        item {
            Text(
                stringResource(R.string.hourly_forecast),
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (hours.size < 24) {
            item {
                Text(
                    stringResource(R.string.available_hours, hours.size)
                )
            }
        }

        item {
            HourlyForecastRow(
                hours = hours,
                formatTime = {
                    formatCityTime(it, weather.timezone)
                }
            )
        }

        item {
            Text(
                stringResource(R.string.daily_forecast),
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(
            items = weather.daily.take(7),
            key = { it.timestamp }
        ) { day ->
            DailyForecastRow(
                day = day,
                dateLabel = Instant.ofEpochSecond(day.timestamp)
                    .atZone(ZoneId.of(weather.timezone))
                    .toLocalDate()
                    .toString()
            )
        }

        item {
            Text(stringResource(R.string.weather_credit))
        }
    }
}
