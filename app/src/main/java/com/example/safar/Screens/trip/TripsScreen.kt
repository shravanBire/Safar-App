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
import com.example.safar.Screens.home.OpenStreetMapViewWithPolyline
import com.example.safar.data.models.LocationData
import com.example.safar.repository.LocationRepository
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.*

@Composable
fun TripsScreen(locationRepository: LocationRepository) {
    var startDate by remember { mutableStateOf<Long?>(null) } // store as millis
    var endDate by remember { mutableStateOf<Long?>(null) }   // store as millis
    var showResults by remember { mutableStateOf(false) }

    var tripLocations by remember { mutableStateOf<List<LocationData>>(emptyList()) }

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
                if (startDate != null && endDate != null && startDate!! <= endDate!!) {
                    coroutineScope.launch {
                        tripLocations = fetchTripData(locationRepository, startDate!!, endDate!!)
                        showResults = true
                    }
                }
            },
            enabled = startDate != null && endDate != null,
        ) {
            Text("Show Trips")
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
        }
    }
}

// -------------------------
// Classic DatePickerDialog for min SDK 24
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

// -------------------------
// Supabase fetch (fetch all → filter locally)
suspend fun fetchTripData(locationRepository: LocationRepository, startMillis: Long, endMillis: Long): List<LocationData> {
    return try {
        val allTrips = locationRepository.getClient()
            .from("locations")
            .select()
            .decodeList<LocationData>()

        // filter locally
        allTrips.filter { loc ->
            val ts = loc.timestamp.toEpochMilliseconds()
            ts in startMillis..(endMillis + 24 * 60 * 60 * 1000) // include end day
        }
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

// -------------------------
// Analytics
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
