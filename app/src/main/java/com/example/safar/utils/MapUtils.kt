package com.example.safar.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import org.maplibre.android.maps.MapView

@SuppressLint("MissingPermission")
suspend fun getCurrentLocation(context: Context): Location? =
    suspendCancellableCoroutine { cont ->
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location -> cont.resume(location) {} }
            .addOnFailureListener { cont.resume(null) {} }
    }

suspend fun recenterToCurrentLocation(mapView: MapView) {
    val context = mapView.context
    val location = getCurrentLocation(context)
    location?.let {
        mapView.getMapAsync { map ->
            val cameraPosition = org.maplibre.android.camera.CameraPosition.Builder()
                .target(org.maplibre.android.geometry.LatLng(it.latitude, it.longitude))
                .zoom(15.0)
                .build()
            map.cameraPosition = cameraPosition
        }
    }
}

