package com.example.safar.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safar.data.models.LocationData
import com.example.safar.repository.DeviceRepository
import com.example.safar.utils.DeviceData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeviceSelectionViewModel(
    private val deviceRepository: DeviceRepository
) : ViewModel() {

    private val _devices = MutableStateFlow<List<DeviceData>>(emptyList())
    val devices = _devices.asStateFlow()

    private val _selectedDevice = MutableStateFlow<DeviceData?>(null)
    val selectedDevice = _selectedDevice.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        loadDevices()
    }

    fun loadDevices() {
        viewModelScope.launch {
            _isLoading.value = true
            deviceRepository.getAllDevices()
                .onSuccess { deviceList ->
                    _devices.value = deviceList
                    // If no device is selected but devices exist, select the first one
                    if (_selectedDevice.value == null && deviceList.isNotEmpty()) {
                        _selectedDevice.value = deviceList.first()
                    }
                    _errorMessage.value = null
                }
                .onFailure { error ->
                    _errorMessage.value = error.message
                    Log.e("SafarApp", "Failed to load devices: ${error.message}")
                }
            _isLoading.value = false
        }
    }

    fun addDevice(deviceId: String, deviceName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            deviceRepository.addDevice(deviceId, deviceName)
                .onSuccess { newDevice ->
                    // Refresh the list
                    loadDevices()
                    _errorMessage.value = null
                    Log.d("SafarApp", "Device added successfully: ${newDevice.device_name}")
                }
                .onFailure { error ->
                    _errorMessage.value = error.message
                    Log.e("SafarApp", "Failed to add device: ${error.message}")
                }
            _isLoading.value = false
        }
    }

    fun selectDevice(device: DeviceData) {
        _selectedDevice.value = device
        Log.d("SafarApp", "Selected device: ${device.device_name} (${device.device_id})")
    }

    fun deleteDevice(deviceId: String) {
        viewModelScope.launch {
            deviceRepository.deleteDevice(deviceId)
                .onSuccess {
                    loadDevices()
                    // If deleted device was selected, select another one
                    if (_selectedDevice.value?.device_id == deviceId) {
                        _selectedDevice.value = _devices.value.firstOrNull()
                    }
                }
                .onFailure { error ->
                    _errorMessage.value = error.message
                }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    suspend fun getLastLocationForDevice(deviceId: String): LocationData? {
        return deviceRepository.getLastLocationForDevice(deviceId)
            .getOrNull()
    }
}