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
import androidx.compose.ui.platform.LocalContext // Add this import
import androidx.compose.ui.unit.dp
import org.maplibre.android.annotations.PolylineOptions

@Composable
fun OpenStreetMapView(
    modifier: Modifier = Modifier,
    bikeLocation: LocationData?
) {
    val context = LocalContext.current
    var marker: Marker? = null

    val mapView = remember {
        MapView(context).apply {
            onCreate(null)
            getMapAsync { mapLibreMap ->
                mapLibreMap.setStyle(
                    Style.Builder().fromUri("https://tiles.openfreemap.org/styles/liberty")
                )
                mapLibreMap.cameraPosition = CameraPosition.Builder()
                    .target(LatLng(20.5937, 78.9629))
                    .zoom(4.0)
                    .build()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mapView.onDestroy()
        }
    }

    AndroidView(
        factory = {
            mapView
        },
        modifier = modifier.fillMaxWidth().height(500.dp),
        update = { managedMapView -> // Use a descriptive name like managedMapView
            bikeLocation?.let { data -> // Use a descriptive name like data
                managedMapView.getMapAsync { map ->
                    marker?.let { oldMarker ->
                        map.removeMarker(oldMarker)
                    }

                    val bikeLatLng = LatLng(data.latitude, data.longitude)

                    marker = map.addMarker(
                        MarkerOptions()
                            .position(bikeLatLng)
                            .title("Bike Location")
                    )

                    val cameraPosition = CameraPosition.Builder()
                        .target(bikeLatLng)
                        .zoom(15.0)
                        .build()
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
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

