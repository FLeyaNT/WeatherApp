package com.example.weatherapp.ui.search

import com.example.weatherapp.ui.common.UiMessage

import com.example.weatherapp.model.City

data class SearchUiState(
    val query: String = "",
    val loading: Boolean = false,
    val cities: List<City> = emptyList(),
    val message: UiMessage? = null,
    val history: List<String> = emptyList(),
    val historyMessage: UiMessage? = null,
    val savingSelection: Boolean = false
)
