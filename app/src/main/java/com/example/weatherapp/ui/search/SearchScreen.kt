package com.example.weatherapp.ui.search

import com.example.weatherapp.R
import androidx.compose.ui.res.stringResource
import com.example.weatherapp.ui.common.localizedText

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapp.model.City
import com.example.weatherapp.ui.search.components.SearchHistorySection

@Composable
fun SearchScreen(
    state: SearchUiState,
    onQueryChange: (String) -> Unit,
    onCitySelected: (City) -> Unit,
    onBack: () -> Unit,
    onHistorySelected: (String) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            TextButton(onClick = onBack) {
                Text(stringResource(R.string.back))
            }

            Text(
                stringResource(R.string.search_city),
                style = MaterialTheme.typography.headlineSmall
            )

            OutlinedTextField(
                value = state.query,
                onValueChange = onQueryChange,
                label = {
                    Text(stringResource(R.string.city_name))
                },
                singleLine = true,
                enabled = !state.savingSelection,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (state.history.isNotEmpty()) {
            item {
                SearchHistorySection(
                    history = state.history,
                    onQuerySelected = onHistorySelected,
                    onClear = onClearHistory,
                    enabled = !state.savingSelection
                )
            }
        }

        state.historyMessage?.let { message ->
            item {
                Text(message.localizedText(), color = MaterialTheme.colorScheme.error)
            }
        }

        if (state.savingSelection) {
            item {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }

        if (state.loading) {
            item {
                CircularProgressIndicator()
            }
        }

        state.message?.let { message ->
            item {
                Text(message.localizedText())
            }
        }

        if (
            !state.loading &&
            state.message == null &&
            state.query.trim().length >= 2 &&
            state.cities.isEmpty()
        ) {
            item {
                Text(stringResource(R.string.no_cities_found))
            }
        }

        items(
            items = state.cities,
            key = { it.id }
        ) { city ->
            OutlinedButton(
                enabled = !state.savingSelection,
                onClick = {
                    onCitySelected(city)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    listOf(
                        city.name,
                        city.country
                    )
                        .filter { it.isNotBlank() }
                        .joinToString(", ")
                )
            }
        }
    }
}
