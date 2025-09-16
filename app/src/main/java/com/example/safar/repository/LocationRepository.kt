// File: com/example/safar/repository/LocationRepository.kt
package com.example.safar.repository

import android.util.Log
import com.example.safar.SupabaseClientProvider
import com.example.safar.data.models.LocationData
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.decodeRecord
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

class LocationRepository {
    private val client = SupabaseClientProvider.client

    fun getClient() = SupabaseClientProvider.client

    // Updated to filter by specific device_id
    fun observeRealtimeBikeLocation(deviceId: String? = null): Flow<LocationData> {
        val channel = client.realtime.channel("realtime:public:locations")

        val changeFlow = channel.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
            table = "locations"
        }

        return changeFlow
            .onStart {
                try {
                    channel.subscribe(blockUntilSubscribed = true)
                    Log.d("SafarApp", "Subscribed to realtime: public.locations")
                } catch (t: Throwable) {
                    Log.e("SafarApp", "Subscribe failed: ${t.message}", t)
                }
            }
            .mapNotNull { action ->
                try {
                    action.decodeRecord<LocationData>()
                } catch (e: Exception) {
                    Log.e("SafarApp", "Failed to decode LocationData from realtime payload: ${e.message}", e)
                    null
                }
            }
            // Filter by device_id if specified
            .filter { locationData ->
                deviceId == null || locationData.device_id == deviceId
            }
            .distinctUntilChanged()
            .onCompletion {
                try {
                    channel.unsubscribe()
                    Log.d("SafarApp", "Unsubscribed from realtime: public.locations")
                } catch (t: Throwable) {
                    Log.w("SafarApp", "Unsubscribe failed: ${t.message}")
                }
            }
    }

    // Get locations for a specific device within date range
    suspend fun getLocationsByDeviceAndDateRange(
        deviceId: String,
        startMillis: Long,
        endMillis: Long
    ): List<LocationData> {
        return try {
            val allTrips = client
                .from("locations")
                .select() {
                    filter {
                        eq("device_id", deviceId)
                    }
                }
                .decodeList<LocationData>()

            // Filter locally by date range
            allTrips.filter { loc ->
                val ts = loc.timestamp.toEpochMilliseconds()
                ts in startMillis..(endMillis + 24 * 60 * 60 * 1000)
            }
        } catch (e: Exception) {
            Log.e("SafarApp", "Failed to fetch locations for device $deviceId: ${e.message}", e)
            emptyList()
        }
    }

    // Get all locations for all devices (for backward compatibility)
    suspend fun getAllLocations(): List<LocationData> {
        return try {
            client
                .from("locations")
                .select()
                .decodeList<LocationData>()
        } catch (e: Exception) {
            Log.e("SafarApp", "Failed to fetch all locations: ${e.message}", e)
            emptyList()
        }
    }
}