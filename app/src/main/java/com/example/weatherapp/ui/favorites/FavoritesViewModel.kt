package com.example.weatherapp.ui.favorites

import com.example.weatherapp.R
import com.example.weatherapp.ui.common.UiMessage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.local.FavoriteChange
import com.example.weatherapp.data.local.FavoritesStorage
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.model.City
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

class FavoritesViewModel(
    private val storage: FavoritesStorage,
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    private val _state = MutableStateFlow(FavoritesUiState())
    val state = _state.asStateFlow()
    private val weatherJobs = mutableMapOf<String, Job>()
    private val requests = Semaphore(3)

    init {
        viewModelScope.launch {
            storage.cities
                .catch { error ->
                    if (error is CancellationException) throw error
                    _state.update {
                        it.copy(reading = false, message = UiMessage(R.string.favorites_read_error))
                    }
                }
                .collect { cities -> updateCities(cities) }
        }
    }

    private fun updateCities(cities: List<City>) {
        val previous = _state.value.cities.associateBy { it.city.id }
        val ids = cities.map { it.id }.toSet()
        weatherJobs.keys.toList().filterNot { it in ids }.forEach {
            weatherJobs.remove(it)?.cancel()
        }
        _state.update { state ->
            state.copy(
                reading = false,
                cities = cities.map { previous[it.id] ?: FavoriteCityUiState(it) }
            )
        }
        cities.filterNot { it.id in previous }.forEach(::refreshCity)
    }

    fun toggleCity(city: City) = changeFavorites {
        if (storage.toggleCity(city) == FavoriteChange.LIMIT_REACHED) {
            _state.update {
                it.copy(message = UiMessage(R.string.favorites_limit, listOf(FavoritesStorage.MAX_CITIES)))
            }
        }
    }

    fun removeCity(city: City) = changeFavorites {
        storage.removeCity(city.id)
    }

    private fun changeFavorites(action: suspend () -> Unit) {
        if (_state.value.saving || _state.value.reading) return
        _state.update { it.copy(saving = true) }
        viewModelScope.launch {
            try {
                action()
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                _state.update { it.copy(message = UiMessage(R.string.favorites_save_error)) }
            } finally {
                _state.update { it.copy(saving = false) }
            }
        }
    }

    fun dismissMessage() {
        _state.update { it.copy(message = null) }
    }

    fun refresh() {
        _state.value.cities.forEach { refreshCity(it.city) }
    }

    fun refreshCity(city: City) {
        if (weatherJobs[city.id]?.isActive == true) return
        if (_state.value.cities.none { it.city.id == city.id }) return
        updateCity(city.id) { it.copy(loading = true, error = null) }

        weatherJobs[city.id] = viewModelScope.launch {
            try {
                val weather = requests.withPermit {
                    weatherRepository.loadWeather(city)
                }
                ensureActive()
                updateCity(city.id) {
                    it.copy(current = weather.current, timezone = weather.timezone, loading = false)
                }
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                updateCity(city.id) {
                    it.copy(loading = false, error = UiMessage(R.string.weather_refresh_error))
                }
            }
        }
    }

    private fun updateCity(id: String, change: (FavoriteCityUiState) -> FavoriteCityUiState) {
        _state.update { state ->
            state.copy(cities = state.cities.map { if (it.city.id == id) change(it) else it })
        }
    }
}
