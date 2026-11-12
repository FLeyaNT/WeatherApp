package com.example.weatherapp.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.data.local.FavoritesStorage
import com.example.weatherapp.data.repository.WeatherRepository

class FavoritesViewModelFactory(
    private val storage: FavoritesStorage,
    private val repository: WeatherRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(FavoritesViewModel::class.java))
        @Suppress("UNCHECKED_CAST")
        return FavoritesViewModel(storage, repository) as T
    }
}
