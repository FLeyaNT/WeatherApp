package com.example.weatherapp.ui.search

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherapp.model.City

@Composable
fun SearchRoute(
    viewModel: SearchViewModel,
    onCitySelected: (City) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold { padding ->
        SearchScreen(
            state = state,
            onQueryChange = viewModel::changeQuery,
            onCitySelected = { city ->
                viewModel.selectCity(city, onCitySelected)
            },
            onBack = onBack,
            onHistorySelected = viewModel::changeQuery,
            onClearHistory = viewModel::clearHistory,
            modifier = Modifier.padding(padding)
        )
    }
}
