package com.example.weatherapp.ui.components

import com.example.weatherapp.R
import androidx.compose.ui.res.stringResource

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription

@Composable
fun WeatherToolbar(
    onSearch: () -> Unit,
    onRefresh: () -> Unit,
    onFavorites: () -> Unit,
    onSettings: () -> Unit
) {
    val settingsDescription = stringResource(R.string.settings)
    Surface(
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onSearch,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 12.dp)
            ) {
                Text(stringResource(R.string.search_city), fontSize = 18.sp, lineHeight = 22.sp, textAlign = TextAlign.Center)
            }

            TextButton(
                onClick = onRefresh,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 12.dp)
            ) {
                Text(stringResource(R.string.refresh), fontSize = 18.sp, lineHeight = 22.sp, textAlign = TextAlign.Center)
            }

            TextButton(
                onClick = onFavorites,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 12.dp)
            ) {
                Text(stringResource(R.string.favorites), fontSize = 18.sp, lineHeight = 22.sp, textAlign = TextAlign.Center)
            }
            IconButton(onClick = onSettings) {
                Text("⚙", fontSize = 24.sp, modifier = Modifier.clearAndSetSemantics {
                    contentDescription = settingsDescription
                })
            }
        }
    }
}
