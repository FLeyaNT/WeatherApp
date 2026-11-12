package com.example.weatherapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.R
import com.example.weatherapp.data.local.SettingsStorage
import com.example.weatherapp.model.AppSettings
import com.example.weatherapp.ui.common.UiMessage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SettingsUiState(
    val settings: AppSettings = AppSettings(),
    val loaded: Boolean = false,
    val saving: Boolean = false,
    val error: UiMessage? = null
)

class SettingsViewModel(
    private val storage: SettingsStorage,
    private val onSettingsChanged: suspend (AppSettings) -> Unit
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            storage.settings.catch { error ->
                if (error is CancellationException) throw error
                _state.update { it.copy(loaded = true, error = UiMessage(R.string.settings_read_error)) }
            }.collect { settings ->
                _state.update { it.copy(settings = settings, loaded = true) }
            }
        }
    }

    fun update(change: (AppSettings) -> AppSettings) {
        if (_state.value.saving) return
        _state.update { it.copy(saving = true, error = null) }
        viewModelScope.launch {
            try {
                storage.update(change)
                onSettingsChanged(storage.settings.first())
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                _state.update { it.copy(error = UiMessage(R.string.settings_save_error)) }
            } finally {
                _state.update { it.copy(saving = false) }
            }
        }
    }
}

class SettingsViewModelFactory(
    private val storage: SettingsStorage,
    private val onSettingsChanged: suspend (AppSettings) -> Unit
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(SettingsViewModel::class.java))
        @Suppress("UNCHECKED_CAST")
        return SettingsViewModel(storage, onSettingsChanged) as T
    }
}
