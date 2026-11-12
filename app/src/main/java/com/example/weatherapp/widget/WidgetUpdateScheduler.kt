package com.example.weatherapp.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object WidgetUpdateScheduler {
    private const val PERIODIC_WORK = "weather_widget_periodic"
    private const val REFRESH_WORK = "weather_widget_refresh"

    fun hasWidgets(context: Context): Boolean = AppWidgetManager.getInstance(context)
        .getAppWidgetIds(ComponentName(context, WeatherWidgetReceiver::class.java)).isNotEmpty()

    fun schedule(context: Context, minutes: Int) {
        if (!hasWidgets(context)) return
        val request = PeriodicWorkRequestBuilder<WeatherWidgetWorker>(minutes.toLong(), TimeUnit.MINUTES)
            .setInitialDelay(minutes.toLong(), TimeUnit.MINUTES)
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_WORK, ExistingPeriodicWorkPolicy.UPDATE, request
        )
    }

    fun refresh(context: Context) {
        if (!hasWidgets(context)) return
        WorkManager.getInstance(context).enqueueUniqueWork(
            REFRESH_WORK, ExistingWorkPolicy.KEEP,
            OneTimeWorkRequestBuilder<WeatherWidgetWorker>().build()
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).apply {
            cancelUniqueWork(PERIODIC_WORK)
            cancelUniqueWork(REFRESH_WORK)
        }
    }
}
