package com.example.weatherapp.util

import com.example.weatherapp.model.*
import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherUnitsTest {
    @Test fun temperatureConversionHandlesFreezingBoilingAndNegativeValues() {
        assertEquals(32.0, temperatureValue(0.0, TemperatureUnit.FAHRENHEIT), 0.0001)
        assertEquals(212.0, temperatureValue(100.0, TemperatureUnit.FAHRENHEIT), 0.0001)
        assertEquals(-40.0, temperatureValue(-40.0, TemperatureUnit.FAHRENHEIT), 0.0001)
        assertEquals(-10.0, temperatureValue(-10.0, TemperatureUnit.CELSIUS), 0.0001)
    }

    @Test fun windConversionMatchesRequestedUnits() {
        assertEquals(10.0, windValue(10.0, WindUnit.MS), 0.0001)
        assertEquals(36.0, windValue(10.0, WindUnit.KMH), 0.0001)
        assertEquals(22.36936, windValue(10.0, WindUnit.MPH), 0.0001)
    }

    @Test fun pressureConversionMatchesStandardAtmosphericPressure() {
        assertEquals(1013.25, pressureValue(1013.25, PressureUnit.HPA), 0.0001)
        assertEquals(760.0, pressureValue(1013.25, PressureUnit.MMHG), 0.01)
        assertEquals(29.9213, pressureValue(1013.25, PressureUnit.INHG), 0.0001)
    }
}
