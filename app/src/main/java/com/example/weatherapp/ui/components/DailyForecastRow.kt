package com.example.weatherapp.ui.components

import com.example.weatherapp.ui.settings.temperatureRangeText

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.weatherapp.model.DailyForecast

@Composable
fun DailyForecastRow(
    day: DailyForecast,
    dateLabel: String
) {
    Row(
        modifier = Modifier.fillMaxWidth().heightIn(min = 32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = dateLabel,
            modifier = Modifier.weight(1.25f),
            maxLines = 1
        )

        Text(
            temperatureRangeText(day.minTemperatureC, day.maxTemperatureC),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            maxLines = 1
        )

        PrecipitationProbability(
            probability = day.precipitationProbability,
            weatherCode = day.weatherCode,
            modifier = Modifier.weight(0.9f),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End)
        )
    }
}
