package com.example.weatherapp.widget

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weatherapp.di.AppContainer
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

class WeatherWidgetWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        if (!WidgetUpdateScheduler.hasWidgets(applicationContext)) {
            WidgetUpdateScheduler.cancel(applicationContext)
            return Result.success()
        }
        val container = AppContainer(applicationContext)
        try {
            val settings = container.settingsStorage.settings.first()
            WidgetUpdateScheduler.schedule(applicationContext, settings.refreshMinutes)
            val request = container.widgetStorage.beginRefresh()
            val weather = try {
                withTimeoutOrNull(45_000L) { container.weatherRepository.loadWeather(request.city) }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.w("WeatherWidget", "Weather refresh failed", error)
                null
            }
            container.widgetStorage.finishRefresh(request, weather)
            WeatherWidget().updateAll(applicationContext)
            return if (weather == null && runAttemptCount < 2) Result.retry() else Result.success()
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            Log.e("WeatherWidget", "Widget update failed", error)
            return if (runAttemptCount < 2) Result.retry() else Result.failure()
        }
    }
}
