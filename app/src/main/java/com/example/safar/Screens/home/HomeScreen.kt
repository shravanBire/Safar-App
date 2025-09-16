// File: com/example/safar/Screens/home/HomeScreen.kt
package com.example.safar.Screens.home

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.safar.Screens.Components.Speedometer
import com.example.safar.Screens.setting.UnitViewModel
import com.example.safar.repository.DeviceRepository
import com.example.safar.repository.LocationRepository
import com.example.safar.utils.DeviceSelectionViewModelFactory
import com.example.safar.viewModels.DeviceSelectionViewModel
import com.example.safar.viewModels.LocationViewModel
import com.example.safar.viewModels.LocationViewModelFactory
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    unitViewModel: UnitViewModel
) {
    val currentUnit = unitViewModel.unit.value

    // Repositories
    val locationRepository = LocationRepository()
    val deviceRepository = DeviceRepository()

    // ViewModels
    val locationViewModel: LocationViewModel = viewModel(
        factory = LocationViewModelFactory(locationRepository)
    )

    val deviceViewModel: DeviceSelectionViewModel = viewModel(
        factory = DeviceSelectionViewModelFactory(deviceRepository)
    )

    // States
    val bikeLocation by locationViewModel.bikeLocation.collectAsState()
    val selectedDevice by deviceViewModel.selectedDevice.collectAsState()
    val currentSpeed = bikeLocation?.speed?.toInt() ?: 0

    var mapLibreMap by remember { mutableStateOf<MapLibreMap?>(null) }
    var showDeviceDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // Start tracking when device is selected
    LaunchedEffect(selectedDevice) {
        selectedDevice?.let { device ->
            locationViewModel.startTrackingDevice(device.device_id)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Home",
                fontSize = 44.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(12.dp)
            )

            // Show selected device info
            selectedDevice?.let { device ->
                Text(
                    text = "Tracking: ${device.device_name}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            OpenStreetMapView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(16.dp)),
                bikeLocation = bikeLocation,
                onMapReady = { map -> mapLibreMap = map }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp)
                .align(Alignment.BottomCenter),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Spacer(modifier = Modifier.height(120.dp))
                    IconButton(
                        onClick = { showDeviceDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Device selection",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                Column {
                    Speedometer(currentSpeed = currentSpeed, unitLabel = currentUnit)
                }
                Column {
                    Spacer(modifier = Modifier.height(120.dp))
                    IconButton(
                        onClick = {
                            bikeLocation?.let { loc ->
                                val target = LatLng(loc.latitude, loc.longitude)
                                mapLibreMap?.animateCamera(
                                    CameraUpdateFactory.newLatLngZoom(target, 15.0)
                                )
                            }
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Current location of vehicle",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }

    // Device Selection Dialog
    if (showDeviceDialog) {
        DeviceSelectionDialog(
            onDismiss = { showDeviceDialog = false },
            onDeviceSelected = { device ->
                deviceViewModel.selectDevice(device)
                // Get and show last location for the selected device
                coroutineScope.launch {
                    val lastLocation = deviceViewModel.getLastLocationForDevice(device.device_id)
                    lastLocation?.let { location ->
                        locationViewModel.updateLocationForSelectedDevice(location)
                        // Center map on last location
                        val target = LatLng(location.latitude, location.longitude)
                        mapLibreMap?.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(target, 15.0)
                        )
                    }
                }
            },
            deviceRepository = deviceRepository
        )
    }
}