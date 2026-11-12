package com.example.weatherapp.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.data.local.SearchHistoryStorage
import com.example.weatherapp.data.local.SettingsStorage
import com.example.weatherapp.data.repository.CityRepository

class SearchViewModelFactory(
    private val repository: CityRepository,
    private val historyStorage: SearchHistoryStorage,
    private val settingsStorage: SettingsStorage
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        require(
            modelClass.isAssignableFrom(
                SearchViewModel::class.java
            )
        )

        @Suppress("UNCHECKED_CAST")
        return SearchViewModel(repository, historyStorage, settingsStorage) as T
    }
}
