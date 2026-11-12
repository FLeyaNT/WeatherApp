package com.example.weatherapp.ui.favorites.components

import com.example.weatherapp.R
import androidx.compose.ui.res.stringResource
import com.example.weatherapp.ui.settings.temperatureText
import com.example.weatherapp.ui.common.localizedText
import com.example.weatherapp.ui.common.cityDisplayName
import com.example.weatherapp.ui.common.cityCountry

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapp.ui.favorites.FavoriteCityUiState
import com.example.weatherapp.util.weatherDescription
import com.example.weatherapp.util.weatherIcon
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun FavoriteCityCard(
    state: FavoriteCityUiState,
    saving: Boolean,
    onOpen: () -> Unit,
    onRemove: () -> Unit,
    onRetry: () -> Unit
) {
    Card(onClick = onOpen, modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(cityDisplayName(state.city), style = MaterialTheme.typography.titleLarge)
                    if (state.city.country.isNotBlank()) Text(cityCountry(state.city))
                }
                TextButton(onClick = onRemove, enabled = !saving) {
                    Text(stringResource(R.string.remove))
                }
            }

            state.current?.let { current ->
                Text(
                    "${weatherIcon(current.weatherCode, current.isDay)} ${temperatureText(current.temperatureC)}",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(stringResource(weatherDescription(current.weatherCode)))
                val time = Instant.ofEpochSecond(current.observedAt)
                    .atZone(ZoneId.of(state.timezone))
                    .format(DateTimeFormatter.ofPattern("dd.MM HH:mm"))
                Text(stringResource(R.string.data_at, time))
            }

            if (state.loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            state.error?.let { error ->
                Text(error.localizedText(), color = MaterialTheme.colorScheme.error)
                TextButton(onClick = onRetry) { Text(stringResource(R.string.retry)) }
            }
            Text(stringResource(R.string.open_forecast_hint))
        }
    }
}
