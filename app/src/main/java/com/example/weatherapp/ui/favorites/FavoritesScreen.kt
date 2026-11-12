package com.example.weatherapp.ui.favorites

import com.example.weatherapp.R
import androidx.compose.ui.res.stringResource

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapp.model.City
import com.example.weatherapp.ui.favorites.components.FavoriteCityCard
import com.example.weatherapp.ui.favorites.components.FavoritesToolbar

@Composable
fun FavoritesScreen(
    state: FavoritesUiState,
    onCitySelected: (City) -> Unit,
    onRemove: (City) -> Unit,
    onRetry: (City) -> Unit,
    onRefresh: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = { FavoritesToolbar(state.cities.size, onBack, onRefresh) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (state.reading) {
                item { CircularProgressIndicator() }
            } else if (state.cities.isEmpty()) {
                item {
                    Text(stringResource(R.string.no_favorites))
                    Text(stringResource(R.string.no_favorites_hint))
                }
            }
            items(state.cities, key = { it.city.id }) { item ->
                FavoriteCityCard(
                    state = item,
                    saving = state.saving,
                    onOpen = { onCitySelected(item.city) },
                    onRemove = { onRemove(item.city) },
                    onRetry = { onRetry(item.city) }
                )
            }
        }
    }
}
