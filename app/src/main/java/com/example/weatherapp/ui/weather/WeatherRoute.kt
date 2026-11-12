package com.example.weatherapp.ui.weather

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherapp.data.location.LocationRepository
import com.example.weatherapp.data.location.ReverseGeocoder
import com.example.weatherapp.ui.components.WeatherErrorContent
import com.example.weatherapp.ui.components.WeatherToolbar
import com.example.weatherapp.ui.location.LocationErrorDialog
import com.example.weatherapp.ui.location.rememberLocationAction
import com.example.weatherapp.ui.favorites.FavoritesViewModel
import com.example.weatherapp.util.weatherDescription
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.ui.common.localizedText

@Composable
fun WeatherRoute(
    viewModel: WeatherViewModel,
    locationRepository: LocationRepository,
    geocoder: ReverseGeocoder,
    favoritesViewModel: FavoritesViewModel,
    onSearch: () -> Unit,
    onFavorites: () -> Unit,
    onSettings: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val favorites by favoritesViewModel.state.collectAsStateWithLifecycle()

    val location = rememberLocationAction(
        repository = locationRepository,
        geocoder = geocoder,
        onCityFound = viewModel::load
    )

    Scaffold(
        topBar = {
            WeatherToolbar(
                onSearch = onSearch,
                onRefresh = viewModel::refresh,
                onFavorites = onFavorites,
                onSettings = onSettings
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = location.request
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_location),
                    contentDescription = stringResource(R.string.locate),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val current = state) {
                WeatherUiState.Initial,
                WeatherUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(
                            Alignment.Center
                        )
                    )
                }

                is WeatherUiState.Success -> {
                    WeatherContent(
                        weather = current.weather,
                        description = stringResource(weatherDescription(
                            current.weather.current.weatherCode
                        )),
                        onLocate = location.request,
                        isFavorite = favorites.contains(current.weather.city),
                        savingFavorite = favorites.saving || favorites.reading,
                        onToggleFavorite = {
                            favoritesViewModel.toggleCity(current.weather.city)
                        },
                        locating = location.locating
                    )
                }

                is WeatherUiState.Error -> {
                    WeatherErrorContent(
                        message = current.message.localizedText(),
                        onRetry = viewModel::refresh
                    )
                }
            }

            if (location.locating) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    location.error?.let { message ->
        LocationErrorDialog(
            message = message.localizedText(),
            onDismiss = location.dismissError
        )
    }
}
