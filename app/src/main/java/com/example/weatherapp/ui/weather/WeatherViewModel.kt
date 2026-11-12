package com.example.weatherapp.ui.weather

import com.example.weatherapp.R
import com.example.weatherapp.ui.common.UiMessage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.model.City
import com.example.weatherapp.model.DefaultCities
import com.example.weatherapp.widget.WeatherWidgetSync
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class WeatherViewModel(
    private val repository: WeatherRepository,
    private val widgetSync: WeatherWidgetSync
) : ViewModel() {

    private val _state =
        MutableStateFlow<WeatherUiState>(WeatherUiState.Initial)

    val state = _state.asStateFlow()

    private var loadingJob: Job? = null
    private var selectedCity: City = DefaultCities.Moscow

    init {
        restoreSelectedCity()
    }

    fun restoreSelectedCity() {
        loadingJob?.cancel()
        loadingJob = viewModelScope.launch {
            selectedCity = widgetSync.selectedCity()
            loadWeather(selectedCity)
        }
    }

    fun load(city: City) {
        selectedCity = city
        loadingJob?.cancel()

        loadingJob = viewModelScope.launch { loadWeather(city) }
    }

    private suspend fun loadWeather(city: City) {
        _state.value = WeatherUiState.Loading
        widgetSync.selectCity(city)
        try {
            val weather = repository.loadWeather(city)
            currentCoroutineContext().ensureActive()
            _state.value = WeatherUiState.Success(weather)
            widgetSync.weatherLoaded(weather)
        } catch (error: CancellationException) {
            throw error
        } catch (_: IOException) {
            showError(city, R.string.weather_network_error)
        } catch (_: HttpException) {
            showError(city, R.string.weather_service_error)
        } catch (_: Exception) {
            showError(city, R.string.weather_response_error)
        }
    }

    private suspend fun showError(city: City, message: Int) {
        _state.value = WeatherUiState.Error(UiMessage(message))
        widgetSync.weatherFailed(city)
    }

    fun refresh() {
        load(selectedCity)
    }

    fun refreshAutomatically() {
        if (loadingJob?.isActive != true) refresh()
    }
}
