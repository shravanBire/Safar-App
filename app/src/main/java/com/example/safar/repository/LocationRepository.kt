package com.example.safar.repository

import android.util.Log // Add this import
import com.example.safar.SupabaseClientProvider
import com.example.safar.data.models.LocationData
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

class LocationRepository {
    private val client = SupabaseClientProvider.client

    suspend fun getLatestBikeLocation(): LocationData? {
        return try {
            val response = client.from("locations")
                .select {
                    order("timestamp", order = Order.DESCENDING)
                    limit(1)
                }
                .decodeSingleOrNull<LocationData>()

            // Add this line to log the response
            Log.d("SafarApp", "Location fetched from Supabase: $response")

            response
        } catch (e: Exception) {
            // Add this log statement to catch any network errors
            Log.e("SafarApp", "Error fetching location from Supabase: ${e.message}", e)
            null
        }
    }
}