package com.example.weatherapp

import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.example.weatherapp.di.AppContainer
import com.example.weatherapp.ui.WeatherApp
import com.example.weatherapp.ui.theme.WeatherAppTheme

class MainActivity : ComponentActivity() {
    private var widgetOpenRequest by mutableIntStateOf(0)

    companion object {
        const val ACTION_SHOW_WEATHER = "com.example.weatherapp.SHOW_WEATHER"
    }

    private val container by lazy {
        AppContainer(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleWidgetIntent(intent)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        setContent {
            WeatherAppTheme {
                WeatherApp(
                    container = container,
                    widgetOpenRequest = widgetOpenRequest
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleWidgetIntent(intent)
    }

    private fun handleWidgetIntent(intent: Intent) {
        if (intent.action == ACTION_SHOW_WEATHER) {
            widgetOpenRequest++
            // Не повторяем переход при пересоздании Activity, например после смены языка.
            intent.action = null
        }
    }
}
