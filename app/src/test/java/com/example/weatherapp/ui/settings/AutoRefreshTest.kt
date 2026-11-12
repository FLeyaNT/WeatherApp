package com.example.weatherapp.ui.settings

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AutoRefreshTest {
    @Test fun waitsForSelectedIntervalAndStopsAfterCancellation() = runTest {
        var count = 0
        val job = backgroundScope.launch { runPeriodicRefresh(15) { count++ } }
        runCurrent()
        advanceTimeBy(15 * 60_000L - 1)
        runCurrent()
        assertEquals(0, count)
        advanceTimeBy(1)
        runCurrent()
        assertEquals(1, count)
        advanceTimeBy(15 * 60_000L)
        runCurrent()
        assertEquals(2, count)
        job.cancel()
        advanceTimeBy(60 * 60_000L)
        runCurrent()
        assertEquals(2, count)
    }
}
