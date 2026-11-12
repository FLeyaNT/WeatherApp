package com.example.weatherapp.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.widget.WeatherWidgetSync

class WeatherViewModelFactory(
    private val repository: WeatherRepository,
    private val widgetSync: WeatherWidgetSync
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        require(
            modelClass.isAssignableFrom(WeatherViewModel::class.java)
        )

        @Suppress("UNCHECKED_CAST")
        return WeatherViewModel(repository, widgetSync) as T
    }
}
