package com.example.weatherapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.weatherapp.model.HourlyForecast

@Composable
fun HourlyForecastRow(
    hours: List<HourlyForecast>,
    formatTime: (Long) -> String
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = hours,
            key = { it.timestamp }
        ) { hour ->
            HourlyForecastCard(
                hour = hour,
                timeLabel = formatTime(hour.timestamp)
            )
        }
    }
}