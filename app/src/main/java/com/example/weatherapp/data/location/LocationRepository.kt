package com.example.weatherapp.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.location.LocationRequest
import com.example.weatherapp.model.AppSettings
import com.example.weatherapp.model.GpsAccuracy
import android.os.CancellationSignal
import androidx.core.content.ContextCompat
import java.io.IOException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.milliseconds

class LocationRepository(context: Context) {

    private val context = context.applicationContext

    private val manager =
        this.context.getSystemService(LocationManager::class.java)

    private fun isGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    suspend fun currentLocation(settings: AppSettings): Location {
        val fineGranted = isGranted(
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        val coarseGranted = isGranted(
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (!fineGranted && !coarseGranted) {
            throw SecurityException(
                "Location permission is missing"
            )
        }

        if (settings.gpsAccuracy == GpsAccuracy.HIGH && !fineGranted) {
            throw SecurityException("Precise location permission is required")
        }

        val providers = locationProviders(settings.gpsAccuracy, fineGranted).filter { provider ->
            manager.allProviders.contains(provider) && manager.isProviderEnabled(provider)
        }

        if (providers.isEmpty()) {
            throw IOException(
                "No enabled location providers"
            )
        }

        val timeoutPerProvider = settings.gpsTimeoutSeconds * 1_000L / providers.size

        for (provider in providers) {
            val location = withTimeoutOrNull(
                timeoutPerProvider.milliseconds
            ) {
                requestLocation(provider, settings.gpsAccuracy, timeoutPerProvider)
            }

            if (location != null) {
                return location
            }
        }

        throw IOException("Location timeout")
    }

    @SuppressLint("MissingPermission")
    private suspend fun requestLocation(
        provider: String,
        accuracy: GpsAccuracy,
        timeoutMillis: Long
    ): Location? {
        return suspendCancellableCoroutine { continuation ->
            val cancellation = CancellationSignal()

            continuation.invokeOnCancellation {
                cancellation.cancel()
            }

            val quality = when (accuracy) {
                GpsAccuracy.HIGH -> LocationRequest.QUALITY_HIGH_ACCURACY
                GpsAccuracy.BALANCED -> LocationRequest.QUALITY_BALANCED_POWER_ACCURACY
                GpsAccuracy.LOW_POWER -> LocationRequest.QUALITY_LOW_POWER
            }
            val request = LocationRequest.Builder(0L)
                .setQuality(quality)
                .setDurationMillis(timeoutMillis)
                .setMaxUpdates(1)
                .build()

            manager.getCurrentLocation(
                provider,
                request,
                cancellation,
                ContextCompat.getMainExecutor(context)
            ) { location ->
                if (continuation.isActive) {
                    continuation.resume(location)
                }
            }
        }
    }
}
