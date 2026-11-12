package com.example.weatherapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.example.weatherapp.model.*
import java.io.File
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.After
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class SettingsStorageTest {
    @get:Rule val folder = TemporaryFolder()
    private val jobs = mutableListOf<Job>()
    private fun file() = File(folder.root, "settings.preferences_pb")
    private fun dataStore(file: File): DataStore<Preferences> {
        val job = SupervisorJob()
        jobs += job
        return PreferenceDataStoreFactory.create(
            scope = CoroutineScope(Dispatchers.IO + job), produceFile = { file }
        )
    }
    @After fun closeStores() = runBlocking { jobs.forEach { it.cancelAndJoin() } }

    @Test fun defaultSettingsAreAvailableOnFirstLaunch() = runBlocking {
        assertEquals(AppSettings(), SettingsStorage(dataStore(file())).settings.first())
    }

    @Test fun everySettingSurvivesReopening() = runBlocking {
        val file = file()
        val storage = SettingsStorage(dataStore(file))
        val expected = AppSettings(
            TemperatureUnit.FAHRENHEIT, WindUnit.MPH, PressureUnit.INHG,
            AppLanguage.ENGLISH, 60, GpsAccuracy.HIGH, 30
        )
        storage.update { expected }
        jobs.last().cancelAndJoin()
        assertEquals(expected, SettingsStorage(dataStore(file)).settings.first())
    }

    @Test fun concurrentChangesPreserveOtherSettings() = runBlocking {
        val storage = SettingsStorage(dataStore(file()))
        coroutineScope {
            launch(Dispatchers.IO) { storage.update { it.copy(temperature = TemperatureUnit.FAHRENHEIT) } }
            launch(Dispatchers.IO) { storage.update { it.copy(language = AppLanguage.ENGLISH) } }
            launch(Dispatchers.IO) { storage.update { it.copy(gpsTimeoutSeconds = 60) } }
        }
        val actual = storage.settings.first()
        assertEquals(TemperatureUnit.FAHRENHEIT, actual.temperature)
        assertEquals(AppLanguage.ENGLISH, actual.language)
        assertEquals(60, actual.gpsTimeoutSeconds)
    }

    @Test fun unsupportedIntervalIsRejectedWithoutChangingSavedValues() = runBlocking {
        val storage = SettingsStorage(dataStore(file()))
        storage.update { it.copy(refreshMinutes = 60) }
        try {
            storage.update { it.copy(refreshMinutes = 0, language = AppLanguage.ENGLISH) }
            fail("Invalid refresh interval must be rejected")
        } catch (_: IllegalArgumentException) { }
        assertEquals(AppSettings(refreshMinutes = 60), storage.settings.first())
    }

    @Test fun unknownStoredValuesFallBackToSafeDefaults() = runBlocking {
        val dataStore = dataStore(file())
        dataStore.edit {
            it[stringPreferencesKey("temperature")] = "UNKNOWN"
            it[intPreferencesKey("gps_timeout_seconds")] = -1
            it[intPreferencesKey("refresh_minutes")] = 1
        }
        assertEquals(AppSettings(), SettingsStorage(dataStore).settings.first())
    }
}
