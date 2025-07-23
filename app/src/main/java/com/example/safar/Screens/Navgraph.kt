package com.example.safar.Screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.safar.Screens.home.HomeScreen
import com.example.safar.Screens.setting.SettingScreen
import com.example.safar.Screens.statistics.StatisticsScreen
import com.example.safar.Screens.trip.TripsScreen


@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController, startDestination = BottomBar.Home.route) {
        composable(BottomBar.Home.route) { HomeScreen() }
        composable(BottomBar.Trips.route) { TripsScreen() }
        composable(BottomBar.Statistics.route) { StatisticsScreen() }
        composable(BottomBar.Settings.route) { SettingScreen() }
    }
}