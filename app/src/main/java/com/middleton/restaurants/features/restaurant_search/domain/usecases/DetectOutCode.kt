package com.middleton.restaurants.features.restaurant_search.domain.usecases

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.LocationServices
import java.util.*
import javax.inject.Inject

class DetectOutCode @Inject constructor(private val context: Context) {
    @SuppressLint("MissingPermission")
    operator fun invoke(onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        var outCode = ""

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                    val postalCode = addresses?.get(0)?.postalCode ?: ""
                    if (postalCode.isNotEmpty()) {
                        outCode = postalCode.substring(0, postalCode.indexOf(" "))
                    }
                    onSuccess(outCode)
                }
            }
            .addOnFailureListener {
                onFailure()
            }
    }
}
