package com.example.safar.Screens.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng

@Composable
fun OpenStreetMapView(modifier: Modifier = Modifier) {
    AndroidView(
        factory = { context ->
            MapView(context).apply {
                onCreate(null)
                getMapAsync { mapLibreMap ->
                    mapLibreMap.setStyle(
                        Style.Builder().fromUri("https://tiles.openfreemap.org/styles/liberty")
                    )
                    mapLibreMap.cameraPosition = CameraPosition.Builder()
                        .target(LatLng(20.9320, 79.0058))
                        .zoom(10.0)
                        .build()
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(500.dp)
    )
}
