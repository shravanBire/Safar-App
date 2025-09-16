package com.example.safar.utils

import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant

@Serializable
data class DeviceData(
    val id: Int,
    val device_id: String,
    val device_name: String,
    val is_active: Boolean = true,
    val created_at: Instant
)