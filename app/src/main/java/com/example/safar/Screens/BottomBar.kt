package com.example.safar.Screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.safar.Screens.home.HomeScreen
import com.example.safar.Screens.setting.SettingScreen
import com.example.safar.Screens.statistics.StatisticsScreen
import com.example.safar.Screens.trip.TripsScreen

sealed class BottomBar(val route: String) {
    object Home : BottomBar("home")
    object Trips : BottomBar("trips")
    object Statistics : BottomBar("statistics")
    object Settings : BottomBar("settings")
}
