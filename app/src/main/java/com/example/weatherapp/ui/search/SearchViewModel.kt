package com.example.weatherapp.ui.search

import com.example.weatherapp.R
import com.example.weatherapp.ui.common.UiMessage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.local.SearchHistoryStorage
import com.example.weatherapp.data.local.SettingsStorage
import com.example.weatherapp.data.repository.CityRepository
import com.example.weatherapp.model.City
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: CityRepository,
    private val historyStorage: SearchHistoryStorage,
    private val settingsStorage: SettingsStorage
) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState())
    val state = _state.asStateFlow()

    private var searchJob: Job? = null
    private var language = "ru"

    init {
        viewModelScope.launch {
            settingsStorage.settings.map { it.language.tag }.distinctUntilChanged()
                .catch { error -> if (error is CancellationException) throw error }
                .collect { tag ->
                    val changed = tag != language
                    language = tag
                    if (changed && !_state.value.savingSelection) changeQuery(_state.value.query)
                }
        }
        viewModelScope.launch {
            historyStorage.history
                .catch { error ->
                    if (error is CancellationException) throw error
                    Log.e("SearchHistory", "Could not read history", error)
                    _state.update {
                        it.copy(historyMessage = UiMessage(R.string.history_read_error))
                    }
                }
                .collect { history ->
                    _state.update { it.copy(history = history) }
                }
        }
    }

    fun changeQuery(query: String) {
        searchJob?.cancel()
        val cleaned = query.trim()

        _state.update {
            it.copy(
                query = query,
                loading = cleaned.length >= 2,
                cities = emptyList(),
                message = null
            )
        }

        if (cleaned.length < 2) return

        searchJob = viewModelScope.launch {
            delay(400)
            performSearch(cleaned)
        }
    }

    fun selectCity(city: City, onSelected: (City) -> Unit) {
        if (_state.value.savingSelection) return
        val query = _state.value.query.trim()

        _state.update {
            it.copy(savingSelection = true, historyMessage = null)
        }

        viewModelScope.launch {
            try {
                historyStorage.addQuery(query)
                onSelected(city)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.e("SearchHistory", "Could not save history", error)
                _state.update {
                    it.copy(historyMessage = UiMessage(R.string.history_save_error))
                }
            } finally {
                _state.update { it.copy(savingSelection = false) }
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            try {
                historyStorage.clear()
                _state.update { it.copy(historyMessage = null) }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.e("SearchHistory", "Could not clear history", error)
                _state.update {
                    it.copy(historyMessage = UiMessage(R.string.history_clear_error))
                }
            }
        }
    }

    private suspend fun performSearch(query: String) {
        try {
            val cities = repository.search(query, language)
            currentCoroutineContext().ensureActive()
            _state.update { it.copy(loading = false, cities = cities) }
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            Log.e("CitySearch", "City search failed", error)
            _state.update {
                it.copy(loading = false, message = searchErrorMessage(error))
            }
        }
    }
}
