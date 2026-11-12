package com.example.weatherapp.data.local

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.example.weatherapp.model.*
import java.io.File
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.After
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class WeatherWidgetStorageTest {
    @get:Rule val folder = TemporaryFolder()
    private val jobs = mutableListOf<Job>()
    private val moscow = DefaultCities.Moscow
    private val otherCity = City("2", "Test city", "Test country", 1.0, 2.0)

    private fun storage(file: File = File(folder.root, "widget.preferences_pb")): WeatherWidgetStorage {
        val job = SupervisorJob()
        jobs += job
        return WeatherWidgetStorage(PreferenceDataStoreFactory.create(
            scope = CoroutineScope(Dispatchers.IO + job), produceFile = { file }
        ))
    }

    @After fun closeStores() = runBlocking { jobs.forEach { it.cancelAndJoin() } }

    @Test fun firstLaunchUsesMoscowWithoutInventingWeather() = runBlocking {
        assertEquals(WeatherWidgetState(), storage().state.first())
    }

    @Test fun selectedCityAndCachedWeatherSurviveReopening() = runBlocking {
        val file = File(folder.root, "persisted.preferences_pb")
        val first = storage(file)
        first.selectCity(otherCity)
        first.saveWeather(weather(otherCity, 5.0))
        jobs.last().cancelAndJoin()
        val restored = storage(file).state.first()
        assertEquals(otherCity, restored.city)
        assertEquals(5.0, restored.snapshot!!.current.temperatureC, 0.0)
        assertFalse(restored.refreshFailed)
    }

    @Test fun failedRefreshKeepsLastSuccessfulWeather() = runBlocking {
        val storage = storage()
        storage.saveWeather(weather(moscow, -3.0))
        storage.finishRefresh(storage.beginRefresh(), null)
        val state = storage.state.first()
        assertEquals(-3.0, state.snapshot!!.current.temperatureC, 0.0)
        assertTrue(state.refreshFailed)
        storage.finishRefresh(storage.beginRefresh(), weather(moscow, 1.0))
        assertFalse(storage.state.first().refreshFailed)
    }

    @Test fun changingCityClearsOldWeatherAndItsError() = runBlocking {
        val storage = storage()
        storage.saveWeather(weather(moscow, -3.0))
        storage.markFailed(moscow)
        storage.selectCity(otherCity)
        assertEquals(WeatherWidgetState(city = otherCity), storage.state.first())
    }

    @Test fun selectingSameCityPreservesCache() = runBlocking {
        val storage = storage()
        storage.saveWeather(weather(moscow, 3.0))
        storage.selectCity(moscow)
        assertEquals(3.0, storage.state.first().snapshot!!.current.temperatureC, 0.0)
    }

    @Test fun responseFromPreviousCityCannotReplaceNewCityWeather() = runBlocking {
        val storage = storage()
        val request = storage.beginRefresh()
        storage.selectCity(otherCity)
        storage.saveWeather(weather(otherCity, 10.0))
        storage.finishRefresh(request, weather(moscow, -5.0))
        val state = storage.state.first()
        assertEquals(otherCity, state.city)
        assertEquals(10.0, state.snapshot!!.current.temperatureC, 0.0)
    }

    @Test fun oldResponseIsRejectedEvenAfterReturningToTheSameCity() = runBlocking {
        val storage = storage()
        val request = storage.beginRefresh()
        storage.selectCity(otherCity)
        storage.selectCity(moscow)
        storage.finishRefresh(request, weather(moscow, -5.0))
        assertNull(storage.state.first().snapshot)
    }

    @Test fun mostRecentRequestWinsWhenRequestsFinishOutOfOrder() = runBlocking {
        val storage = storage()
        val older = storage.beginRefresh()
        val newer = storage.beginRefresh()
        storage.finishRefresh(newer, weather(moscow, 10.0))
        storage.finishRefresh(older, weather(moscow, -5.0))
        assertEquals(10.0, storage.state.first().snapshot!!.current.temperatureC, 0.0)
    }

    @Test fun appUpdateCannotBeOverwrittenByEarlierWorkerFailure() = runBlocking {
        val storage = storage()
        val request = storage.beginRefresh()
        storage.saveWeather(weather(moscow, 10.0))
        storage.finishRefresh(request, null)
        val state = storage.state.first()
        assertEquals(10.0, state.snapshot!!.current.temperatureC, 0.0)
        assertFalse(state.refreshFailed)
    }

    @Test fun olderObservationFromTheAppCannotReplaceNewerWorkerData() = runBlocking {
        val storage = storage()
        val older = weather(moscow, 3.0)
        val newer = older.copy(current = older.current.copy(temperatureC = 8.0, observedAt = older.current.observedAt + 900))
        storage.finishRefresh(storage.beginRefresh(), newer)
        storage.saveWeather(older)
        assertEquals(8.0, storage.state.first().snapshot!!.current.temperatureC, 0.0)
    }

    private fun weather(city: City, temperature: Double) = Weather(
        city, "Europe/Moscow",
        CurrentWeather(temperature, temperature - 2, 70, 3.5, 1000.0, 10000.0, 2, true, 1794402000),
        emptyList(), emptyList()
    )
}
