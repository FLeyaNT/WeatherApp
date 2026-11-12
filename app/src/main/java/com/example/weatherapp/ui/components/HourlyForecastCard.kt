package com.example.weatherapp.ui.components

import com.example.weatherapp.ui.settings.temperatureText

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapp.model.HourlyForecast

@Composable
fun HourlyForecastCard(
    hour: HourlyForecast,
    timeLabel: String
) {
    Card(modifier = Modifier.width(96.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(timeLabel)
            Text(temperatureText(hour.temperatureC))
            PrecipitationProbability(
                probability = hour.precipitationProbability,
                weatherCode = hour.weatherCode,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                reserveIconSpace = false
            )
        }
    }
}
