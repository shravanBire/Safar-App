// File: com/example/safar/repository/LocationRepository.kt
package com.example.safar.repository

import android.util.Log
import com.example.safar.SupabaseClientProvider
import com.example.safar.data.models.LocationData
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.PostgresAction.Insert
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.realtime.createChannel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.distinctUntilChanged

class LocationRepository {
    private val client = SupabaseClientProvider.client

    fun getRealtimeBikeLocation(): Flow<LocationData?> {
        // 1. Create a channel for your table
        val channel = client.realtime.createChannel("bike_locations_channel")

        // 2. Subscribe to the channel
        channel.subscribe()

        // 3. Get the flow of changes from the channel
        return channel.postgresChangeFlow<Insert>(
            schema = "public",
            table = "locations"
        )
            .mapNotNull { action ->
                try {
                    val location = action.decodeRecord<LocationData>()
                    Log.d("SafarApp", "Realtime update: $location")
                    location
                } catch (e: Exception) {
                    Log.e("SafarApp", "Error decoding realtime update", e)
                    null
                }
            }
            .distinctUntilChanged()
    }
}