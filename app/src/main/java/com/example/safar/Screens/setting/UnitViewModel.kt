package com.example.safar.Screens.setting

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class UnitViewModel : ViewModel() {
    var unit = mutableStateOf("km/h")
        private set

    fun setToKilometers() {
        unit.value = "km/h"
    }

    fun setToMiles() {
        unit.value = "mph"
    }
}