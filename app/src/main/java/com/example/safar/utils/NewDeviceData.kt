package com.example.safar.utils

import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant
@Serializable
data class NewDeviceData(
    val device_id: String,
    val device_name: String,
    val is_active: Boolean = true
)
