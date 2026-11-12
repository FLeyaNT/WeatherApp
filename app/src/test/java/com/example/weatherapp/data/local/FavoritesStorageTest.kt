package com.example.weatherapp.data.local

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.example.weatherapp.model.City
import com.example.weatherapp.model.favoriteId
import java.io.File
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.After
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class FavoritesStorageTest {
    @get:Rule val temporaryFolder = TemporaryFolder()
    private val jobs = mutableListOf<Job>()

    private fun city(id: String) = City(id, "Город $id", "Россия", 55.0, 37.0)
    private fun file() = File(temporaryFolder.root, "favorites.preferences_pb")

    private fun storage(file: File): FavoritesStorage {
        val job = SupervisorJob()
        jobs += job
        return FavoritesStorage(PreferenceDataStoreFactory.create(
            scope = CoroutineScope(Dispatchers.IO + job),
            produceFile = { file }
        ))
    }

    @After fun closeStores() = runBlocking {
        jobs.forEach { it.cancelAndJoin() }
    }

    @Test fun enforcesTenCityLimitAndAllowsAddingAfterRemoval() = runBlocking {
        val storage = storage(file())
        (1..10).forEach { storage.toggleCity(city(it.toString())) }
        assertEquals(FavoriteChange.LIMIT_REACHED, storage.toggleCity(city("11")))
        assertEquals(10, storage.cities.first().size)

        storage.removeCity("1")
        assertEquals(FavoriteChange.ADDED, storage.toggleCity(city("11")))
        assertEquals(10, storage.cities.first().size)
        assertFalse(storage.cities.first().any { it.id == "1" })
    }

    @Test fun togglingExistingCityRemovesItWithoutDuplicates() = runBlocking {
        val storage = storage(file())
        assertEquals(FavoriteChange.ADDED, storage.toggleCity(city("1")))
        assertEquals(FavoriteChange.REMOVED, storage.toggleCity(city("1")))
        assertTrue(storage.cities.first().isEmpty())
    }

    @Test fun savedCitiesAndDeletionsSurviveReopening() = runBlocking {
        val file = file()
        val original = storage(file)
        original.toggleCity(city("1"))
        original.toggleCity(city("2"))
        original.removeCity("1")
        jobs.last().cancelAndJoin()

        assertEquals(listOf(city("2")), storage(file).cities.first())
    }

    @Test fun concurrentUpdatesCannotExceedLimit() = runBlocking {
        val storage = storage(file())
        val results = coroutineScope {
            (1..20).map { id ->
                async(Dispatchers.IO) { storage.toggleCity(city(id.toString())) }
            }.awaitAll()
        }
        assertEquals(10, results.count { it == FavoriteChange.ADDED })
        assertEquals(10, results.count { it == FavoriteChange.LIMIT_REACHED })
        assertEquals(10, storage.cities.first().map { it.id }.distinct().size)
    }

    @Test fun differentGpsLocationsAreSavedAsIndependentFixedPlaces() = runBlocking {
        val storage = storage(file())
        val first = city("gps")
        val second = first.copy(latitude = 60.0)
        storage.toggleCity(first)
        storage.toggleCity(second)

        assertEquals(
            listOf(first.copy(id = first.favoriteId()), second.copy(id = second.favoriteId())),
            storage.cities.first()
        )
        storage.toggleCity(first)
        assertEquals(listOf(second.favoriteId()), storage.cities.first().map { it.id })
    }
}
