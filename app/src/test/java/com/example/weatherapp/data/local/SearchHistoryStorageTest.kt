package com.example.weatherapp.data.local

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class SearchHistoryStorageTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val jobs = mutableListOf<kotlinx.coroutines.Job>()

    private fun storage(file: File): SearchHistoryStorage {
        val job = SupervisorJob()
        jobs += job
        val dataStore = PreferenceDataStoreFactory.create(
            scope = CoroutineScope(Dispatchers.IO + job),
            produceFile = { file }
        )
        return SearchHistoryStorage(dataStore)
    }

    private fun historyFile() = File(
        temporaryFolder.root,
        "history.preferences_pb"
    )

    @After
    fun closeStores() = runBlocking {
        jobs.forEach { it.cancelAndJoin() }
    }

    @Test
    fun keepsFiveNewestQueriesInOrder() = runBlocking {
        val storage = storage(historyFile())
        listOf("Москва", "Казань", "Омск", "Тула", "Пермь", "Уфа")
            .forEach { storage.addQuery(it) }

        assertEquals(
            listOf("Уфа", "Пермь", "Тула", "Омск", "Казань"),
            storage.history.first()
        )
    }

    @Test
    fun trimsQueriesAndMovesDuplicatesToFrontIgnoringCase() = runBlocking {
        val storage = storage(historyFile())
        storage.addQuery("Москва")
        storage.addQuery("Казань")
        storage.addQuery("  москва  ")
        storage.addQuery("   ")

        assertEquals(listOf("москва", "Казань"), storage.history.first())
    }

    @Test
    fun historySurvivesReopeningTheStore() = runBlocking {
        val file = historyFile()
        val original = storage(file)
        original.addQuery("Москва")
        original.addQuery("Казань")
        jobs.last().cancelAndJoin()

        val reopened = storage(file)
        assertEquals(listOf("Казань", "Москва"), reopened.history.first())
    }

    @Test
    fun clearingHistoryIsPersisted() = runBlocking {
        val file = historyFile()
        val original = storage(file)
        original.addQuery("Москва")
        original.clear()
        assertTrue(original.history.first().isEmpty())
        jobs.last().cancelAndJoin()

        assertTrue(storage(file).history.first().isEmpty())
    }

    @Test
    fun concurrentWritesDoNotLoseQueries() = runBlocking {
        val storage = storage(historyFile())
        val queries = listOf("Москва", "Казань", "Омск", "Тула", "Пермь")

        coroutineScope {
            queries.forEach { query ->
                launch(Dispatchers.IO) { storage.addQuery(query) }
            }
        }

        assertEquals(queries.toSet(), storage.history.first().toSet())
        assertEquals(5, storage.history.first().size)
    }
}
