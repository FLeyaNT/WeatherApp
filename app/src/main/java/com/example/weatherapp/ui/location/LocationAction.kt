package com.example.weatherapp.ui.location

import com.example.weatherapp.ui.common.UiMessage

data class LocationAction(
    val locating: Boolean,
    val error: UiMessage?,
    val request: () -> Unit,
    val dismissError: () -> Unit
)