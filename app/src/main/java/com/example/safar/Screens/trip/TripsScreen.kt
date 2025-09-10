// TripScreen.kt
package com.example.safar.Screens.trips

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safar.Screens.home.OpenStreetMapViewWithPolyline
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TripsScreen() {
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var showResults by remember { mutableStateOf(false) }

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
            DateSelector("Start Date", startDate) { selected ->
                startDate = selected
                showResults = false
            }
            Spacer(modifier = Modifier.width(16.dp))
            DateSelector("End Date", endDate) { selected ->
                endDate = selected
                showResults = false
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (startDate != null && endDate != null && startDate!! <= endDate!!) {
                    showResults = true
                }
            },
            enabled = startDate != null && endDate != null,
        ) {
            Text("Show Trips")
        }

        if (showResults) {
            Spacer(modifier = Modifier.height(24.dp))

            Text("Distance: 12.5 km", fontWeight = FontWeight.Medium)
            Text("Duration: 34 mins", fontWeight = FontWeight.Medium)

            Spacer(modifier = Modifier.height(16.dp))

            OpenStreetMapViewWithPolyline(
                coordinates = dummyPathPoints(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }
    }
}

fun dummyPathPoints(): List<Pair<Double, Double>> {
    return listOf(
        19.0760 to 72.8777, // Mumbai
        19.2183 to 72.9781, // A bit ahead
        19.3000 to 73.0000  // End point
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateSelector(
    label: String,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    val today = remember { LocalDate.now() }

    Button(onClick = {
        // Simulated date picker
        onDateSelected(today.minusDays((0..7).random().toLong()))
    }) {
        Text(text = selectedDate?.toString() ?: label)
    }
}

