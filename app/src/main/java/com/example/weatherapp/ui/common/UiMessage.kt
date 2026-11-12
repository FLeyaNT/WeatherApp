package com.example.weatherapp.ui.common

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

// Храним ссылку на строку, чтобы уже показанная ошибка тоже меняла язык.
data class UiMessage(@param:StringRes val resourceId: Int, val arguments: List<Any> = emptyList())

@Composable
fun UiMessage.localizedText(): String = stringResource(resourceId, *arguments.toTypedArray())
