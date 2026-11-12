package com.example.weatherapp.data.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import com.example.weatherapp.model.City
import java.util.Locale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.milliseconds

class ReverseGeocoder(context: Context) {

    private val context = context.applicationContext

    suspend fun resolve(location: Location, language: String = "ru"): City {
        val address = try {
            findAddress(location, language)
        } catch (error: CancellationException) {
            throw error
        } catch (_: Exception) {
            null
        }

        return City(
            id = "gps",
            name = address?.locality
                ?: address?.subAdminArea
                ?: address?.adminArea
                ?: if (language == "en") "My location" else "Моя локация",
            country = address?.countryName.orEmpty(),
            latitude = location.latitude,
            longitude = location.longitude
        )
    }

    private suspend fun findAddress(
        location: Location,
        language: String
    ): Address? {
        if (!Geocoder.isPresent()) return null

        return withTimeoutOrNull(3_000L.milliseconds) {
            suspendCancellableCoroutine { continuation ->
                val geocoder = Geocoder(
                    context,
                    Locale.forLanguageTag(language)
                )

                geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1,
                    object : Geocoder.GeocodeListener {
                        override fun onGeocode(
                            addresses: MutableList<Address>
                        ) {
                            if (continuation.isActive) {
                                continuation.resume(
                                    addresses.firstOrNull()
                                )
                            }
                        }

                        override fun onError(
                            errorMessage: String?
                        ) {
                            if (continuation.isActive) {
                                continuation.resume(null)
                            }
                        }
                    }
                )
            }
        }
    }
}
