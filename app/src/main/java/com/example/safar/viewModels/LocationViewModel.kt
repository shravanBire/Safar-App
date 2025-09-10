package com.example.safar.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safar.data.models.LocationData
import com.example.safar.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationViewModel(
    private val repo: LocationRepository
) : ViewModel() {

    private val _bikeLocation = MutableStateFlow<LocationData?>(null)
    val bikeLocation = _bikeLocation.asStateFlow()

    fun fetchLatestBikeLocation() {
        viewModelScope.launch {
            val location = repo.getLatestBikeLocation()
            _bikeLocation.value = location
        }
    }
}
