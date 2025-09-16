// File: com/example/safar/repository/LocationRepository.kt
package com.example.safar.repository

import android.util.Log
import com.example.safar.SupabaseClientProvider
import com.example.safar.data.models.LocationData
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.decodeRecord
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

class LocationRepository {

    private val client = SupabaseClientProvider.client

    fun getClient() = SupabaseClientProvider.client

    fun observeRealtimeBikeLocation(): Flow<LocationData> {
        // Create a channel (name can be any unique string)
        val channel = client.realtime.channel("realtime:public:locations")

        // Create a flow that listens to INSERT actions for the "locations" table
        val changeFlow = channel.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
            table = "locations" // configure the table inside the lambda (docs example)
        }

        return changeFlow
            // subscribe to the channel when collection starts (subscribe is suspend, onStart is suspend-capable)
            .onStart {
                try {
                    channel.subscribe(blockUntilSubscribed = true)
                    Log.d("SafarApp", "Subscribed to realtime: public.locations")
                } catch (t: Throwable) {
                    Log.e("SafarApp", "Subscribe failed: ${t.message}", t)
                }
            }
            // convert PostgresAction.Insert -> LocationData (decodeRecord is provided by the SDK)
            .mapNotNull { action ->
                try {
                    // decodeRecord<T>() returns the typed object from the record payload
                    action.decodeRecord<LocationData>()
                } catch (e: Exception) {
                    Log.e("SafarApp", "Failed to decode LocationData from realtime payload: ${e.message}", e)
                    null
                }
            }
            .distinctUntilChanged()
            // unsubscribe when the flow completes (onCompletion is suspend-capable)
            .onCompletion {
                try {
                    // safe best-effort unsubscribe
                    channel.unsubscribe()
                    Log.d("SafarApp", "Unsubscribed from realtime: public.locations")
                } catch (t: Throwable) {
                    Log.w("SafarApp", "Unsubscribe failed: ${t.message}")
                }
            }
    }
}
