package com.example.safar.Screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.safar.repository.DeviceRepository
import com.example.safar.utils.DeviceData
import com.example.safar.utils.DeviceSelectionViewModelFactory
import com.example.safar.viewModels.DeviceSelectionViewModel
import kotlinx.coroutines.launch

@Composable
fun DeviceSelectionDialog(
    onDismiss: () -> Unit,
    onDeviceSelected: (DeviceData) -> Unit,
    deviceRepository: DeviceRepository
) {
    val deviceViewModel: DeviceSelectionViewModel = viewModel(
        factory = DeviceSelectionViewModelFactory(deviceRepository)
    )

    val devices by deviceViewModel.devices.collectAsState()
    val selectedDevice by deviceViewModel.selectedDevice.collectAsState()
    val isLoading by deviceViewModel.isLoading.collectAsState()
    val errorMessage by deviceViewModel.errorMessage.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Select Device",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Error message
                errorMessage?.let { error ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = error,
                            modifier = Modifier.padding(8.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Loading indicator
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    // Device list
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(devices) { device ->
                            DeviceItem(
                                device = device,
                                isSelected = device.id == selectedDevice?.id,
                                onDeviceClick = {
                                    deviceViewModel.selectDevice(device)
                                    coroutineScope.launch {
                                        // Get last location and show on map
                                        val lastLocation = deviceViewModel.getLastLocationForDevice(device.device_id)
                                        onDeviceSelected(device)
                                        onDismiss()
                                    }
                                },
                                onDeleteClick = {
                                    deviceViewModel.deleteDevice(device.device_id)
                                }
                            )
                        }

                        if (devices.isEmpty()) {
                            item {
                                Text(
                                    "No devices found. Add a device first.",
                                    modifier = Modifier.padding(16.dp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Refresh button
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { deviceViewModel.loadDevices() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Refresh")
                }
            }
        }
    }
}

@Composable
fun DeviceItem(
    device: DeviceData,
    isSelected: Boolean,
    onDeviceClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDeviceClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "Device",
                    tint = if (device.is_active)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = device.device_name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "ID: ${device.device_id}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!device.is_active) {
                        Text(
                            text = "Inactive",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete device",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}