package com.example.weatherapp.widget

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import com.example.weatherapp.di.AppContainer
import kotlinx.coroutines.flow.first

class WeatherWidget : GlanceAppWidget() {
    override val stateDefinition = null
    override val sizeMode = SizeMode.Responsive(setOf(DpSize(180.dp, 160.dp), DpSize(270.dp, 210.dp)))

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val container = AppContainer(context)
        val initialState = container.widgetStorage.state.first()
        val initialSettings = container.settingsStorage.settings.first()
        provideContent {
            val state by container.widgetStorage.state.collectAsState(initialState)
            val settings by container.settingsStorage.settings.collectAsState(initialSettings)
            WeatherWidgetContent(state, settings)
        }
    }
}
