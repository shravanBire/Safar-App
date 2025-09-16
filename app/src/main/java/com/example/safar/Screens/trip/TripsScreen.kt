// File: com/example/safar/Screens/trips/TripsScreen.kt
package com.example.safar.Screens.trips

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.safar.Screens.home.OpenStreetMapViewWithPolyline
import com.example.safar.data.models.LocationData
import com.example.safar.repository.DeviceRepository
import com.example.safar.repository.LocationRepository
import com.example.safar.utils.DeviceSelectionViewModelFactory
import com.example.safar.viewModels.DeviceSelectionViewModel
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(locationRepository: LocationRepository) {
    var startDate by remember { mutableStateOf<Long?>(null) }
    var endDate by remember { mutableStateOf<Long?>(null) }
    var showResults by remember { mutableStateOf(false) }
    var tripLocations by remember { mutableStateOf<List<LocationData>>(emptyList()) }

    // Device selection
    val deviceRepository = DeviceRepository()
    val deviceViewModel: DeviceSelectionViewModel = viewModel(
        factory = DeviceSelectionViewModelFactory(deviceRepository)
    )

    val selectedDevice by deviceViewModel.selectedDevice.collectAsState()
    val devices by deviceViewModel.devices.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Trip History",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Device Selection Dropdown
        if (devices.isNotEmpty()) {
            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedDevice?.device_name ?: "Select Device",
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Select Device") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    devices.forEach { device ->
                        DropdownMenuItem(
                            text = { Text(device.device_name) },
                            onClick = {
                                deviceViewModel.selectDevice(device)
                                expanded = false
                                showResults = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Date Selection
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            DateSelector("Start Date", startDate) { selectedMillis ->
                startDate = selectedMillis
                showResults = false
            }
            Spacer(modifier = Modifier.width(16.dp))
            DateSelector("End Date", endDate) { selectedMillis ->
                endDate = selectedMillis
                showResults = false
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                selectedDevice?.let { device ->
                    if (startDate != null && endDate != null && startDate!! <= endDate!!) {
                        coroutineScope.launch {
                            tripLocations = fetchTripDataForDevice(
                                locationRepository,
                                device.device_id,
                                startDate!!,
                                endDate!!
                            )
                            showResults = true
                        }
                    }
                }
            },
            enabled = startDate != null && endDate != null && selectedDevice != null,
        ) {
            Text("Show Trips")
        }

        if (selectedDevice == null) {
            Text(
                "Please select a device first",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }

        if (showResults && tripLocations.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))

            val distance = calculateTotalDistance(tripLocations)
            val duration = calculateDuration(tripLocations)
            val averageSpeed = if (duration > 0) distance / duration * 60 else 0.0
            val maxSpeed = tripLocations.maxOfOrNull { it.speed ?: 0.0 } ?: 0.0

            Text("Distance: ${"%.2f".format(distance)} km", fontWeight = FontWeight.Medium)
            Text("Duration: ${duration.toInt()} mins", fontWeight = FontWeight.Medium)
            Text("Average Speed: ${"%.2f".format(averageSpeed)} km/h", fontWeight = FontWeight.Medium)
            Text("Max Speed: ${"%.2f".format(maxSpeed)} km/h", fontWeight = FontWeight.Medium)

            Spacer(modifier = Modifier.height(16.dp))

            OpenStreetMapViewWithPolyline(
                coordinates = tripLocations.map { it.latitude to it.longitude },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        } else if (showResults && tripLocations.isEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "No trip data found for selected dates",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DateSelector(
    label: String,
    selectedDateMillis: Long?,
    onDateSelected: (Long) -> Unit
) {
    val context = LocalContext.current
    var showPicker by remember { mutableStateOf(false) }

    if (showPicker) {
        val calendar = Calendar.getInstance()
        selectedDateMillis?.let { calendar.timeInMillis = it }

        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                calendar.set(year, month, day)
                onDateSelected(calendar.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
        showPicker = false
    }

    Button(onClick = { showPicker = true }) {
        val display = selectedDateMillis?.let {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it))
        }
        Text(text = display ?: label)
    }
}

// Updated fetch function for specific device
suspend fun fetchTripDataForDevice(
    locationRepository: LocationRepository,
    deviceId: String,
    startMillis: Long,
    endMillis: Long
): List<LocationData> {
    return try {
        locationRepository.getLocationsByDeviceAndDateRange(deviceId, startMillis, endMillis)
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

fun calculateTotalDistance(locations: List<LocationData>): Double {
    if (locations.size < 2) return 0.0
    var total = 0.0
    for (i in 0 until locations.size - 1) {
        val lat1 = locations[i].latitude
        val lon1 = locations[i].longitude
        val lat2 = locations[i + 1].latitude
        val lon2 = locations[i + 1].longitude
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(lat1)) *
                cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        total += r * c
    }
    return total
}

fun calculateDuration(locations: List<LocationData>): Double {
    if (locations.size < 2) return 0.0
    val startMillis = locations.first().timestamp.toEpochMilliseconds()
    val endMillis = locations.last().timestamp.toEpochMilliseconds()
    return ((endMillis - startMillis) / 60000.0) // minutes
}