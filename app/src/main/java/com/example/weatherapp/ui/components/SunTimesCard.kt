package com.example.weatherapp.ui.components

import com.example.weatherapp.R
import androidx.compose.ui.res.stringResource

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.model.DailyForecast
import com.example.weatherapp.util.formatCityTime

@Composable
fun SunTimesCard(
    forecast: DailyForecast,
    timezone: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🌅", fontSize = 24.sp)
                Text(stringResource(R.string.sunrise, formatCityTime(forecast.sunrise, timezone)))
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🌇", fontSize = 24.sp)
                Text(stringResource(R.string.sunset, formatCityTime(forecast.sunset, timezone)))
            }
        }
    }
}
