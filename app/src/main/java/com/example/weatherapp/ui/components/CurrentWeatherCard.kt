package com.example.weatherapp.ui.components

import com.example.weatherapp.R
import androidx.compose.ui.res.stringResource
import com.example.weatherapp.ui.settings.temperatureText
import com.example.weatherapp.ui.settings.windText
import com.example.weatherapp.ui.settings.pressureText
import com.example.weatherapp.ui.settings.visibilityText

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.model.CurrentWeather
import com.example.weatherapp.ui.favorites.components.FavoriteCityButton
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.util.weatherIcon

@Composable
fun CurrentWeatherCard(
    cityName: String,
    weather: CurrentWeather,
    description: String,
    isFavorite: Boolean,
    savingFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    isGpsLocation: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = cityName,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (isGpsLocation) {
                        Icon(
                            painter = painterResource(R.drawable.ic_location),
                            contentDescription = stringResource(R.string.my_location),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                FavoriteCityButton(
                    isFavorite = isFavorite,
                    busy = savingFavorite,
                    onClick = onToggleFavorite
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = temperatureText(weather.temperatureC),
                    style = MaterialTheme.typography.displayLarge,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = weatherIcon(weather.weatherCode, weather.isDay),
                    style = MaterialTheme.typography.displayMedium,
                    fontSize = 60.sp,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Text(
                text = description,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = stringResource(R.string.feels_like, temperatureText(weather.feelsLikeC))
            )

            HorizontalDivider()

            Text(stringResource(R.string.humidity, weather.humidityPercent))
            Text(stringResource(R.string.wind, windText(weather.windSpeedMs)))
            Text(stringResource(R.string.pressure, pressureText(weather.pressureHpa)))

            val visibility = visibilityText(weather.visibilityMeters)

            Text(stringResource(R.string.visibility, visibility))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrentWeatherCardPreview() {
    WeatherAppTheme {
        CurrentWeatherCard(
            cityName = "Москва",
            description = "Облачно",
            isFavorite = false,
            savingFavorite = false,
            onToggleFavorite = {},
            weather = CurrentWeather(
                temperatureC = 8.0,
                feelsLikeC = 5.0,
                humidityPercent = 76,
                windSpeedMs = 3.5,
                pressureHpa = 1013.0,
                visibilityMeters = 10000.0,
                weatherCode = 3,
                isDay = true,
                observedAt = 0L
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
