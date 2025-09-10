package com.example.safar.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.safar.repository.LocationRepository

class LocationViewModelFactory(private val repo: LocationRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LocationViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}