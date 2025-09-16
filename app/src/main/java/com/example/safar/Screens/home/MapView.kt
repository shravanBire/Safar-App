package com.example.safar.Screens.home

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import com.example.safar.data.models.LocationData
import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraUpdateFactory
import androidx.compose.runtime.remember
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext // Add this import
import androidx.compose.ui.unit.dp
import org.maplibre.android.annotations.PolylineOptions
import org.maplibre.android.maps.MapLibreMap

@Composable
fun OpenStreetMapView(
    modifier: Modifier = Modifier,
    bikeLocation: LocationData?,
    onMapReady: (MapLibreMap) -> Unit = {}
) {
    val context = LocalContext.current
    var marker: Marker? by remember { mutableStateOf(null) }
    var mapLibreMap: MapLibreMap? by remember { mutableStateOf(null) }

    val mapView = remember {
        MapView(context).apply {
            onCreate(null)
            getMapAsync { map ->
                mapLibreMap = map
                onMapReady(map) // return map ref to HomeScreen
                map.setStyle(
                    Style.Builder().fromUri("https://tiles.openfreemap.org/styles/liberty")
                ) {
                    // Initial camera
                    map.cameraPosition = CameraPosition.Builder()
                        .target(LatLng(20.5937, 78.9629))
                        .zoom(4.0)
                        .build()
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mapView.onDestroy()
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier.fillMaxWidth().height(500.dp),
        update = {
            bikeLocation?.let { data ->
                val bikeLatLng = LatLng(data.latitude, data.longitude)

                mapLibreMap?.let { map ->
                    if (marker == null) {
                        // First time: create marker
                        marker = map.addMarker(
                            MarkerOptions()
                                .position(bikeLatLng)
                                .title("Bike Location")
                        )
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(bikeLatLng, 15.0))
                    } else {
                        // ✅ Only update position instead of creating new marker
                        marker?.position = bikeLatLng
                    }
                }
            }
        }
    )
}

@Composable
fun OpenStreetMapViewWithPolyline(
    coordinates: List<Pair<Double, Double>>,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            MapView(context).apply {
                onCreate(null)
                getMapAsync { mapLibreMap ->
                    mapLibreMap.setStyle(
                        Style.Builder().fromUri("https://tiles.openfreemap.org/styles/liberty")
                    ) {
                        val latLngList = coordinates.map { LatLng(it.first, it.second) }

                        // Set camera to first point if available
                        if (latLngList.isNotEmpty()) {
                            mapLibreMap.cameraPosition = CameraPosition.Builder()
                                .target(latLngList.first())
                                .zoom(14.0)
                                .build()
                        }

                        // Add polyline
                        mapLibreMap.addPolyline(
                            PolylineOptions()
                                .addAll(latLngList)
                                .color(Color.BLUE)
                                .width(5f)
                        )
                    }
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
    )
}