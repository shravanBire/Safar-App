package com.example.safar

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

object SupabaseClientProvider {
    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = "https://pwoudpmlvicwwiqzvkqs.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB3b3VkcG1sdmljd3dpcXp2a3FzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTU0NTM1MTIsImV4cCI6MjA3MTAyOTUxMn0.ePYy9aFctOsPfNy_iHOojTtNfUCNlbHvz26ALAh-sMU"
        ) {
            install(Postgrest)
            install(Realtime)
        }
    }
}

