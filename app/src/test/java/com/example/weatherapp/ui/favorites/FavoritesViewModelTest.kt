package com.example.weatherapp.ui.favorites

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.local.FavoritesStorage
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.model.*
import java.io.IOException
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val storage = FavoritesStorage(InMemoryPreferences())
    private val repository = TestWeatherRepository()
    private var viewModel: FavoritesViewModel? = null

    @Before fun setUp() { Dispatchers.setMain(dispatcher) }
    @After fun tearDown() {
        viewModel?.viewModelScope?.cancel()
        Dispatchers.resetMain()
    }

    private fun city(id: String) = City(id, id, "Россия", 55.0, 37.0)
    private fun createViewModel() = FavoritesViewModel(storage, repository).also {
        viewModel = it
    }

    @Test fun oneFailedCityDoesNotHideTemperaturesOfOtherCities() = runTest(dispatcher) {
        storage.toggleCity(city("good"))
        storage.toggleCity(city("failed"))
        val model = createViewModel()
        advanceUntilIdle()

        val rows = model.state.value.cities.associateBy { it.city.id }
        assertEquals(12.0, rows.getValue("good").current!!.temperatureC, 0.001)
        assertNull(rows.getValue("good").error)
        assertNotNull(rows.getValue("failed").error)
        assertFalse(rows.values.any { it.loading })
    }

    @Test fun removingCityCancelsItsRequestAndDoesNotRestoreItLater() = runTest(dispatcher) {
        storage.toggleCity(city("slow"))
        val model = createViewModel()
        runCurrent()
        assertTrue(model.state.value.cities.single().loading)

        model.removeCity(city("slow"))
        advanceUntilIdle()

        assertTrue(model.state.value.cities.isEmpty())
        assertTrue("slow" in repository.cancelled)
        assertTrue(storage.cities.first().isEmpty())
    }

    @Test fun limitIsShownAndConcurrentWeatherRequestsAreBounded() = runTest(dispatcher) {
        (1..10).forEach { storage.toggleCity(city(it.toString())) }
        val model = createViewModel()
        advanceUntilIdle()
        model.toggleCity(city("11"))
        advanceUntilIdle()

        assertEquals(10, model.state.value.cities.size)
        assertNotNull(model.state.value.message)
        assertTrue(repository.maxActive <= 3)
        assertEquals(10, repository.loaded.size)
    }

    @Test fun refreshUpdatesTemperatureAndKeepsLastValueOnNetworkFailure() = runTest(dispatcher) {
        storage.toggleCity(city("good"))
        val model = createViewModel()
        advanceUntilIdle()

        repository.failing += "good"
        model.refresh()
        advanceUntilIdle()
        assertEquals(12.0, model.state.value.cities.single().current!!.temperatureC, 0.001)
        assertNotNull(model.state.value.cities.single().error)

        repository.failing.clear()
        repository.temperature = 22.0
        model.refresh()
        advanceUntilIdle()
        assertEquals(22.0, model.state.value.cities.single().current!!.temperatureC, 0.001)
        assertNull(model.state.value.cities.single().error)
    }

    private class InMemoryPreferences : DataStore<Preferences> {
        private val preferences = MutableStateFlow<Preferences>(emptyPreferences())
        private val mutex = Mutex()
        override val data = preferences.asStateFlow()
        override suspend fun updateData(
            transform: suspend (Preferences) -> Preferences
        ): Preferences = mutex.withLock {
            transform(preferences.value).also { preferences.value = it }
        }
    }

    private class TestWeatherRepository : WeatherRepository {
        val loaded = mutableSetOf<String>()
        val cancelled = mutableSetOf<String>()
        val failing = mutableSetOf("failed")
        var temperature = 12.0
        private var active = 0
        var maxActive = 0

        override suspend fun loadWeather(city: City): Weather {
            active++
            maxActive = maxOf(maxActive, active)
            try {
                delay(1_000)
                if (city.id in failing) throw IOException("Test failure")
                loaded += city.id
                return Weather(
                    city, "Europe/Moscow",
                    CurrentWeather(temperature, 10.0, 60, 3.0, 1010.0, 10000.0, 0, true, 1_700_000_000L),
                    emptyList(), emptyList()
                )
            } catch (error: CancellationException) {
                cancelled += city.id
                throw error
            } finally {
                active--
            }
        }
    }
}
