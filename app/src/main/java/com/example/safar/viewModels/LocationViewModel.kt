// File: com/example/safar/viewModels/LocationViewModel.kt
package com.example.safar.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safar.data.models.LocationData
import com.example.safar.repository.LocationRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class LocationViewModel(
    private val repo: LocationRepository
) : ViewModel() {

    private val _bikeLocation = MutableStateFlow<LocationData?>(null)
    val bikeLocation = _bikeLocation.asStateFlow()

    private var realtimeJob: Job? = null
    private var currentDeviceId: String? = null

    init {
        Log.i("SafarApp", "LocationViewModel initialized")
    }

    fun startTrackingDevice(deviceId: String?) {
        // Stop previous tracking
        stopTracking()

        currentDeviceId = deviceId

        if (deviceId != null) {
            Log.i("SafarApp", "Starting to track device: $deviceId")
            realtimeJob = viewModelScope.launch {
                repo.observeRealtimeBikeLocation(deviceId)
                    .catch { e ->
                        Log.e("SafarApp", "Error collecting realtime location for device $deviceId: ${e.message}", e)
                    }
                    .collect { location ->
                        Log.d("SafarApp", "New location received for device $deviceId: $location")
                        _bikeLocation.value = location
                    }
            }
        } else {
            Log.i("SafarApp", "No device selected, stopping tracking")
            _bikeLocation.value = null
        }
    }

    fun stopTracking() {
        realtimeJob?.cancel()
        realtimeJob = null
        Log.i("SafarApp", "Stopped tracking device: $currentDeviceId")
    }

    fun updateLocationForSelectedDevice(location: LocationData?) {
        _bikeLocation.value = location
    }

    override fun onCleared() {
        super.onCleared()
        stopTracking()
    }
}

