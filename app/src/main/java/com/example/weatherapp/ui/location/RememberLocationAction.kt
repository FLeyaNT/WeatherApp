package com.example.weatherapp.ui.location

import com.example.weatherapp.R
import com.example.weatherapp.model.GpsAccuracy
import com.example.weatherapp.ui.common.UiMessage
import com.example.weatherapp.ui.settings.LocalAppSettings
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.weatherapp.data.location.LocationRepository
import com.example.weatherapp.data.location.ReverseGeocoder
import com.example.weatherapp.model.City
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
fun rememberLocationAction(
    repository: LocationRepository,
    geocoder: ReverseGeocoder,
    onCityFound: (City) -> Unit
): LocationAction {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val settings by rememberUpdatedState(LocalAppSettings.current)

    val currentOnCityFound by rememberUpdatedState(onCityFound)

    var locating by remember {
        mutableStateOf(false)
    }

    var error by remember {
        mutableStateOf<UiMessage?>(null)
    }

    val locate: () -> Unit = {
        if (!locating) {
            locating = true
            error = null

            scope.launch {
                try {
                    val coordinates =
                        repository.currentLocation(settings)

                    val city = geocoder.resolve(coordinates, settings.language.tag)

                    currentOnCityFound(city)
                } catch (exception: CancellationException) {
                    throw exception
                } catch (_: Exception) {
                    error =
                        UiMessage(R.string.location_error)
                } finally {
                    locating = false
                }
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

        val coarseGranted =
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineGranted || (coarseGranted && settings.gpsAccuracy != GpsAccuracy.HIGH)) {
            locate()
        } else {
            error = UiMessage(
                if (coarseGranted && settings.gpsAccuracy == GpsAccuracy.HIGH) {
                    R.string.precise_location_required
                } else {
                    R.string.location_permission_denied
                }
            )
        }
    }

    val request: () -> Unit = {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || (coarseGranted && settings.gpsAccuracy != GpsAccuracy.HIGH)) {
            locate()
        } else {
            launcher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    return LocationAction(
        locating = locating,
        error = error,
        request = request,
        dismissError = { error = null }
    )
}