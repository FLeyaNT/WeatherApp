package com.example.weatherapp.ui.search.components

import com.example.weatherapp.R
import androidx.compose.ui.res.stringResource

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SearchHistorySection(
    history: List<String>,
    onQuerySelected: (String) -> Unit,
    onClear: () -> Unit,
    enabled: Boolean = true
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.search_history), style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = onClear, enabled = enabled) {
                Text(stringResource(R.string.clear))
            }
        }

        history.forEach { query ->
            TextButton(
                onClick = { onQuerySelected(query) },
                enabled = enabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(query, modifier = Modifier.weight(1f))
            }
        }
    }
}
