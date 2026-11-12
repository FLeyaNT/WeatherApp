package com.example.weatherapp.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherapp.R
import com.example.weatherapp.model.*
import com.example.weatherapp.ui.common.localizedText
import com.example.weatherapp.ui.settings.components.SettingsChoice

@Composable
fun SettingsRoute(viewModel: SettingsViewModel, onBack: () -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SettingsScreen(state, viewModel::update, onBack)
}

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onChange: ((AppSettings) -> AppSettings) -> Unit,
    onBack: () -> Unit
) {
    val settings = state.settings
    val enabled = state.loaded && !state.saving
    Scaffold(topBar = {
        Surface(tonalElevation = 3.dp) {
            Column(Modifier.fillMaxWidth().statusBarsPadding().padding(8.dp)) {
                TextButton(onClick = onBack) { Text(stringResource(R.string.back)) }
                Text(stringResource(R.string.settings), style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(horizontal = 8.dp))
            }
        }
    }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            state.error?.let { error -> item { Text(error.localizedText(), color = MaterialTheme.colorScheme.error) } }
            item {
                SettingsChoice(stringResource(R.string.temperature_setting), settings.temperature,
                    listOf(TemperatureUnit.CELSIUS to stringResource(R.string.celsius),
                        TemperatureUnit.FAHRENHEIT to stringResource(R.string.fahrenheit)), enabled
                ) { unit -> onChange { it.copy(temperature = unit) } }
            }
            item {
                SettingsChoice(stringResource(R.string.wind_setting), settings.wind,
                    listOf(WindUnit.MS to stringResource(R.string.unit_ms),
                        WindUnit.KMH to stringResource(R.string.unit_kmh),
                        WindUnit.MPH to stringResource(R.string.unit_mph)), enabled
                ) { unit -> onChange { it.copy(wind = unit) } }
            }
            item {
                SettingsChoice(stringResource(R.string.pressure_setting), settings.pressure,
                    listOf(PressureUnit.MMHG to stringResource(R.string.unit_mmhg),
                        PressureUnit.HPA to stringResource(R.string.unit_hpa),
                        PressureUnit.INHG to stringResource(R.string.unit_inhg)), enabled
                ) { unit -> onChange { it.copy(pressure = unit) } }
            }
            item {
                SettingsChoice(stringResource(R.string.language_setting), settings.language,
                    listOf(AppLanguage.RUSSIAN to stringResource(R.string.language_russian),
                        AppLanguage.ENGLISH to stringResource(R.string.language_english)), enabled
                ) { language -> onChange { it.copy(language = language) } }
            }
            item {
                SettingsChoice(stringResource(R.string.refresh_setting), settings.refreshMinutes,
                    AppSettings.refreshOptions.map { it to stringResource(R.string.refresh_option, it) }, enabled
                ) { minutes -> onChange { it.copy(refreshMinutes = minutes) } }
                Text(stringResource(R.string.refresh_hint), modifier = Modifier.padding(8.dp))
            }
            item {
                SettingsChoice(stringResource(R.string.gps_accuracy_setting), settings.gpsAccuracy,
                    listOf(GpsAccuracy.HIGH to stringResource(R.string.gps_high),
                        GpsAccuracy.BALANCED to stringResource(R.string.gps_balanced),
                        GpsAccuracy.LOW_POWER to stringResource(R.string.gps_low)), enabled
                ) { accuracy -> onChange { it.copy(gpsAccuracy = accuracy) } }
                Text(stringResource(R.string.gps_accuracy_hint), modifier = Modifier.padding(8.dp))
            }
            item {
                SettingsChoice(stringResource(R.string.gps_timeout_setting), settings.gpsTimeoutSeconds,
                    AppSettings.timeoutOptions.map { it to stringResource(R.string.timeout_option, it) }, enabled
                ) { seconds -> onChange { it.copy(gpsTimeoutSeconds = seconds) } }
            }
        }
    }
}
