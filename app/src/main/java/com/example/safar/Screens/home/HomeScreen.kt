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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.safar.Screens.Components.Speedometer
import com.example.safar.Screens.setting.UnitViewModel
import com.example.safar.repository.LocationRepository
import com.example.safar.viewModels.LocationViewModel
import com.example.safar.viewModels.LocationViewModelFactory
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import org.maplibre.android.maps.MapView

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    unitViewModel: UnitViewModel
) {
    val currentUnit = unitViewModel.unit.value

    val locationRepository = LocationRepository()

    val locationViewModel: LocationViewModel = viewModel(
        factory = LocationViewModelFactory(locationRepository)
    )

    val bikeLocation by locationViewModel.bikeLocation.collectAsState()

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

            OpenStreetMapView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(16.dp)),
                bikeLocation = bikeLocation
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
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Wi-Fi status",
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
                Speedometer(currentSpeed = 30, unitLabel = currentUnit)
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(
                    onClick = { locationViewModel.fetchLatestBikeLocation() },
                    modifier = Modifier.padding(16.dp)
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