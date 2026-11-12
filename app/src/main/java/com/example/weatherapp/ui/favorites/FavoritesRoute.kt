package com.example.weatherapp.ui.favorites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherapp.model.City

@Composable
fun FavoritesRoute(
    viewModel: FavoritesViewModel,
    onCitySelected: (City) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) { viewModel.refresh() }

    FavoritesScreen(
        state = state,
        onCitySelected = onCitySelected,
        onRemove = viewModel::removeCity,
        onRetry = viewModel::refreshCity,
        onRefresh = viewModel::refresh,
        onBack = onBack
    )
}
