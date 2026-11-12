package com.example.weatherapp.data.location

import com.example.weatherapp.model.GpsAccuracy
import org.junit.Assert.*
import org.junit.Test

class LocationProviderPolicyTest {
    @Test fun highAccuracyUsesGpsAndRequiresPrecisePermission() {
        assertEquals(listOf("gps"), locationProviders(GpsAccuracy.HIGH, true))
        assertTrue(locationProviders(GpsAccuracy.HIGH, false).isEmpty())
    }
    @Test fun balancedUsesNetworkThenGpsWhenPreciseLocationIsAllowed() {
        assertEquals(listOf("network", "gps"), locationProviders(GpsAccuracy.BALANCED, true))
        assertEquals(listOf("network"), locationProviders(GpsAccuracy.BALANCED, false))
    }
    @Test fun powerSavingNeverRequestsGps() {
        assertEquals(listOf("network"), locationProviders(GpsAccuracy.LOW_POWER, true))
        assertEquals(listOf("network"), locationProviders(GpsAccuracy.LOW_POWER, false))
    }
}
