package com.example.weatherapp.ui.settings

import android.app.LocaleManager
import android.os.LocaleList
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.di.AppContainer

@Composable
fun SettingsProvider(container: AppContainer, content: @Composable (SettingsViewModel) -> Unit) {
    val factory = remember(container) {
        SettingsViewModelFactory(container.settingsStorage, container.widgetSync::settingsChanged)
    }
    val settingsViewModel: SettingsViewModel = viewModel(factory = factory)
    val state by settingsViewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(state.loaded, state.settings.language) {
        if (state.loaded) {
            val manager = context.getSystemService(LocaleManager::class.java)
            val locales = LocaleList.forLanguageTags(state.settings.language.tag)
            if (manager.applicationLocales != locales) manager.applicationLocales = locales
        }
    }

    if (!state.loaded) {
        Surface(Modifier.fillMaxSize()) {
            Box(contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        }
    } else {
        CompositionLocalProvider(LocalAppSettings provides state.settings) {
            content(settingsViewModel)
        }
    }
}
