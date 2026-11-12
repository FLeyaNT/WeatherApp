package com.example.weatherapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight

private val DefaultTypography = Typography()

// Единая жирность текста для всех экранов, карточек и кнопок.
val Typography = Typography(
    displayLarge = DefaultTypography.displayLarge.copy(fontWeight = FontWeight.Bold),
    displayMedium = DefaultTypography.displayMedium.copy(fontWeight = FontWeight.Bold),
    displaySmall = DefaultTypography.displaySmall.copy(fontWeight = FontWeight.Bold),
    headlineLarge = DefaultTypography.headlineLarge.copy(fontWeight = FontWeight.Bold),
    headlineMedium = DefaultTypography.headlineMedium.copy(fontWeight = FontWeight.Bold),
    headlineSmall = DefaultTypography.headlineSmall.copy(fontWeight = FontWeight.Bold),
    titleLarge = DefaultTypography.titleLarge.copy(fontWeight = FontWeight.Bold),
    titleMedium = DefaultTypography.titleMedium.copy(fontWeight = FontWeight.Bold),
    titleSmall = DefaultTypography.titleSmall.copy(fontWeight = FontWeight.Bold),
    bodyLarge = DefaultTypography.bodyLarge.copy(fontWeight = FontWeight.Bold),
    bodyMedium = DefaultTypography.bodyMedium.copy(fontWeight = FontWeight.Bold),
    bodySmall = DefaultTypography.bodySmall.copy(fontWeight = FontWeight.Bold),
    labelLarge = DefaultTypography.labelLarge.copy(fontWeight = FontWeight.Bold),
    labelMedium = DefaultTypography.labelMedium.copy(fontWeight = FontWeight.Bold),
    labelSmall = DefaultTypography.labelSmall.copy(fontWeight = FontWeight.Bold)
)
