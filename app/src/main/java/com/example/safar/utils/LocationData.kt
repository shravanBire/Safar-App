package com.example.safar.data.models

import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant // Import the Instant class

@Serializable
data class LocationData(
    val id: Int,
    val device_id: String,
    val latitude: Double,
    val longitude: Double,
    val speed: Double? = null,
    val timestamp: Instant,
)