package com.example.safar.repository

import android.util.Log
import com.example.safar.SupabaseClientProvider
import com.example.safar.data.models.LocationData
import com.example.safar.utils.DeviceData
import com.example.safar.utils.NewDeviceData
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeviceRepository {
    private val client = SupabaseClientProvider.client

    suspend fun addDevice(deviceId: String, deviceName: String): Result<DeviceData> {
        return withContext(Dispatchers.IO) {
            try {
                val newDevice = NewDeviceData(
                    device_id = deviceId,
                    device_name = deviceName,
                    is_active = true
                )

                val response = client
                    .from("devices")
                    .insert(newDevice) {
                        select()
                    }
                    .decodeSingle<DeviceData>()

                Log.d("SafarApp", "Device added successfully: $response")
                Result.success(response)
            } catch (e: Exception) {
                Log.e("SafarApp", "Failed to add device: ${e.message}", e)
                Result.failure(e)
            }
        }
    }

    // Alternative approach using raw map
    suspend fun addDeviceAlternative(deviceId: String, deviceName: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                client
                    .from("devices")
                    .insert(
                        mapOf(
                            "device_id" to deviceId,
                            "device_name" to deviceName,
                            "is_active" to true
                        )
                    )

                Log.d("SafarApp", "Device added successfully")
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("SafarApp", "Failed to add device: ${e.message}", e)
                Result.failure(e)
            }
        }
    }

    suspend fun getAllDevices(): Result<List<DeviceData>> {
        return withContext(Dispatchers.IO) {
            try {
                val devices = client
                    .from("devices")
                    .select()
                    .decodeList<DeviceData>()

                Log.d("SafarApp", "Fetched ${devices.size} devices")
                Result.success(devices)
            } catch (e: Exception) {
                Log.e("SafarApp", "Failed to fetch devices: ${e.message}", e)
                Result.failure(e)
            }
        }
    }

    suspend fun updateDeviceStatus(deviceId: String, isActive: Boolean): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                client
                    .from("devices")
                    .update(mapOf("is_active" to isActive)) {
                        filter {
                            eq("device_id", deviceId)
                        }
                    }

                Log.d("SafarApp", "Device $deviceId status updated to $isActive")
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("SafarApp", "Failed to update device status: ${e.message}", e)
                Result.failure(e)
            }
        }
    }

    suspend fun deleteDevice(deviceId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                client
                    .from("devices")
                    .delete {
                        filter {
                            eq("device_id", deviceId)
                        }
                    }

                Log.d("SafarApp", "Device $deviceId deleted successfully")
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("SafarApp", "Failed to delete device: ${e.message}", e)
                Result.failure(e)
            }
        }
    }

    suspend fun getLastLocationForDevice(deviceId: String): Result<LocationData?> {
        return withContext(Dispatchers.IO) {
            try {
                val locations = client
                    .from("locations")
                    .select() {
                        filter {
                            eq("device_id", deviceId)
                        }
                        order("timestamp", Order.DESCENDING)
                        limit(1)
                    }
                    .decodeList<LocationData>()

                val location = locations.firstOrNull()
                Log.d("SafarApp", "Last location for device $deviceId: $location")
                Result.success(location)
            } catch (e: Exception) {
                Log.e("SafarApp", "Failed to get last location for device $deviceId: ${e.message}", e)
                Result.failure(e)
            }
        }
    }
}