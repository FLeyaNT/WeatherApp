package com.example.weatherapp.widget

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.updateAll
import com.example.weatherapp.data.local.WeatherWidgetStorage
import com.example.weatherapp.model.AppSettings
import com.example.weatherapp.model.City
import com.example.weatherapp.model.DefaultCities
import com.example.weatherapp.model.Weather
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first

/** Связывает изменения в приложении с виджетом, не прерывая загрузку экрана при сбое виджета. */
class WeatherWidgetSync(private val context: Context, private val storage: WeatherWidgetStorage) {
    suspend fun selectedCity(): City = try {
        storage.state.first().city
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        Log.e("WeatherWidget", "Cannot restore selected city", error)
        DefaultCities.Moscow
    }

    suspend fun selectCity(city: City) = safely {
        storage.selectCity(city)
        redraw()
    }

    suspend fun weatherLoaded(weather: Weather) = safely {
        storage.saveWeather(weather)
        redraw()
    }

    suspend fun weatherFailed(city: City) = safely {
        storage.markFailed(city)
        redraw()
    }

    suspend fun settingsChanged(settings: AppSettings) = safely {
        WidgetUpdateScheduler.schedule(context, settings.refreshMinutes)
        redraw()
    }

    private suspend fun redraw() {
        if (WidgetUpdateScheduler.hasWidgets(context)) WeatherWidget().updateAll(context)
    }

    private suspend fun safely(action: suspend () -> Unit) {
        try {
            action()
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            Log.e("WeatherWidget", "Cannot synchronize widget", error)
        }
    }
}
