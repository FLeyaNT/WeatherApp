package com.example.weatherapp.ui.favorites.components

import com.example.weatherapp.R
import androidx.compose.ui.res.stringResource

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapp.data.local.FavoritesStorage

@Composable
fun FavoritesToolbar(count: Int, onBack: () -> Unit, onRefresh: () -> Unit) {
    Surface(tonalElevation = 3.dp) {
        Column(modifier = Modifier.statusBarsPadding().padding(horizontal = 8.dp)) {
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                TextButton(onClick = onBack) { Text(stringResource(R.string.back)) }
                TextButton(onClick = onRefresh) { Text(stringResource(R.string.refresh_temperatures)) }
            }
            Text(
                stringResource(R.string.favorites_count, count, FavoritesStorage.MAX_CITIES),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
