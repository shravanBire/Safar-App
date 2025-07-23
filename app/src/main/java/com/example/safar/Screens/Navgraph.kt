package com.example.safar.Screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.safar.Screens.home.HomeScreen
import com.example.safar.Screens.setting.SettingScreen
import com.example.safar.Screens.setting.UnitScreen
import com.example.safar.Screens.statistics.StatisticsScreen
import com.example.safar.Screens.trip.TripsScreen
import com.example.safar.viewModels.ThemeViewModel


@Composable
fun AppNavigation(
    navController: NavHostController,
    themeViewModel: ThemeViewModel,
    innerPadding: PaddingValues // 👈 Add this parameter
) {
    NavHost(
        navController = navController,
        startDestination = BottomBar.Home.route,
        modifier = Modifier.padding(innerPadding) // 👈 Apply padding here
    ) {
        composable(BottomBar.Home.route) { HomeScreen() }
        composable(BottomBar.Trips.route) { TripsScreen() }
        composable(BottomBar.Statistics.route) { StatisticsScreen() }
        composable(BottomBar.Settings.route) { SettingScreen(navController, themeViewModel = themeViewModel) }
        composable("units"){ UnitScreen(navController)}
    }
}
