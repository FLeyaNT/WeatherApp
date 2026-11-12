package com.example.weatherapp.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.data.local.WeatherWidgetState
import com.example.weatherapp.data.local.WidgetSnapshot
import com.example.weatherapp.model.AppSettings
import com.example.weatherapp.util.weatherDescription
import com.example.weatherapp.util.weatherIcon

private val foreground = ColorProvider(Color(0xFFF4EFFA))
private val secondary = ColorProvider(Color(0xFFC9C3D3))

@Composable
internal fun WeatherWidgetContent(state: WeatherWidgetState, settings: AppSettings) {
    val context = LocalContext.current.widgetContext(settings)
    val expanded = LocalSize.current.height >= 210.dp && LocalSize.current.width >= 270.dp
    val openApp = Intent(context, MainActivity::class.java)
        .setAction(MainActivity.ACTION_SHOW_WEATHER)
        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
    Column(
        modifier = GlanceModifier.fillMaxSize().appWidgetBackground()
            .background(Color(0xFF191B20)).cornerRadius(24.dp)
            .clickable(actionStartActivity(openApp)).padding(12.dp)
    ) {
        WidgetHeader(context, state)
        val snapshot = state.snapshot
        if (snapshot == null) {
            Spacer(GlanceModifier.height(10.dp))
            Text(context.getString(if (state.refreshFailed) R.string.widget_load_error else R.string.widget_empty),
                style = widgetTextStyle(14), maxLines = 3)
        } else {
            WidgetTemperature(snapshot, settings)
            Text(context.getString(weatherDescription(snapshot.current.weatherCode)),
                style = widgetTextStyle(14), maxLines = 1)
            if (expanded) WidgetDetails(context, snapshot, settings)
            Spacer(GlanceModifier.defaultWeight())
            WidgetStatus(context, state, settings)
        }
    }
}

@Composable
private fun WidgetHeader(context: Context, state: WeatherWidgetState) {
    Row(modifier = GlanceModifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(widgetCityName(context, state.city), modifier = GlanceModifier.defaultWeight(),
            style = widgetTextStyle(18), maxLines = 1)
        if (state.city.id == "gps") {
            Image(ImageProvider(R.drawable.ic_location), context.getString(R.string.my_location),
                modifier = GlanceModifier.size(18.dp), colorFilter = ColorFilter.tint(foreground))
        }
    }
}

@Composable
private fun WidgetTemperature(snapshot: WidgetSnapshot, settings: AppSettings) {
    Row(modifier = GlanceModifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(widgetTemperature(snapshot.current.temperatureC, settings),
            modifier = GlanceModifier.defaultWeight(), style = widgetTextStyle(32))
        Text(weatherIcon(snapshot.current.weatherCode, snapshot.current.isDay), style = widgetTextStyle(52))
    }
}

@Composable
private fun WidgetDetails(context: Context, snapshot: WidgetSnapshot, settings: AppSettings) {
    Spacer(GlanceModifier.height(6.dp))
    Text(context.getString(R.string.feels_like, widgetTemperature(snapshot.current.feelsLikeC, settings)),
        style = widgetTextStyle(13), maxLines = 1)
    Row(modifier = GlanceModifier.fillMaxWidth()) {
        Text(context.getString(R.string.wind, widgetWind(context, snapshot.current.windSpeedMs, settings)),
            modifier = GlanceModifier.defaultWeight(), style = widgetTextStyle(12), maxLines = 1)
        Text(context.getString(R.string.humidity, snapshot.current.humidityPercent),
            style = widgetTextStyle(12), maxLines = 1)
    }
}

@Composable
private fun WidgetStatus(context: Context, state: WeatherWidgetState, settings: AppSettings) {
    if (state.refreshFailed) {
        Text(context.getString(R.string.widget_refresh_failed), style = widgetTextStyle(11), maxLines = 1)
    }
    state.snapshot?.let { snapshot ->
        Text(context.getString(R.string.widget_data_at,
            widgetDataTime(snapshot.current.observedAt, snapshot.timezone, settings)),
            style = TextStyle(color = secondary, fontSize = 11.sp, fontWeight = FontWeight.Bold), maxLines = 1)
    }
}

private fun widgetTextStyle(size: Int) = TextStyle(
    color = foreground, fontSize = size.sp, fontWeight = FontWeight.Bold
)
