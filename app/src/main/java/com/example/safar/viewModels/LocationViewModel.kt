// File: com/example/safar/viewModels/LocationViewModel.kt
package com.example.safar.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safar.data.models.LocationData
import com.example.safar.repository.LocationRepository
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

    init {
        Log.i("SafarApp", "LocationViewModel is being initialized.")

        // Collect realtime updates from the repository
        viewModelScope.launch {
            repo.observeRealtimeBikeLocation()
                .catch { e ->
                    Log.e("SafarApp", "Error collecting realtime location: ${e.message}", e)
                }
                .collect { location ->
                    Log.d("SafarApp", "New location received in ViewModel: $location")
                    _bikeLocation.value = location
                }
        }
    }
}
