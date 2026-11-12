package com.example.weatherapp.ui.settings

import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.currentCoroutineContext

@Composable
fun AutoRefreshEffect(intervalMinutes: Int, onRefresh: () -> Unit) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentRefresh by rememberUpdatedState(onRefresh)
    LaunchedEffect(lifecycle, intervalMinutes) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            runPeriodicRefresh(intervalMinutes) { currentRefresh() }
        }
    }
}

internal suspend fun runPeriodicRefresh(intervalMinutes: Int, onRefresh: () -> Unit) {
    require(intervalMinutes > 0)
    while (currentCoroutineContext().isActive) {
        delay(intervalMinutes * 60_000L)
        onRefresh()
    }
}
