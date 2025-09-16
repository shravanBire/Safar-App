package com.example.safar.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.safar.repository.DeviceRepository
import com.example.safar.viewModels.DeviceSelectionViewModel

class DeviceSelectionViewModelFactory(
    private val deviceRepository: DeviceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeviceSelectionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DeviceSelectionViewModel(deviceRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}