package com.example.safar.Screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.safar.Screens.home.HomeScreen
import com.example.safar.Screens.setting.SettingScreen
import com.example.safar.Screens.setting.UnitScreen
import com.example.safar.Screens.setting.UnitViewModel
import com.example.safar.Screens.trips.TripsScreen
import com.example.safar.repository.LocationRepository
import com.example.safar.viewModels.ThemeViewModel
import org.maplibre.android.maps.MapView


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    navController: NavHostController,
    themeViewModel: ThemeViewModel,
    innerPadding: PaddingValues,
    unitViewModel: UnitViewModel,
    locationRepository: LocationRepository

) {
    NavHost(
        navController = navController,
        startDestination = BottomBar.Home.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(BottomBar.Home.route) { HomeScreen(unitViewModel) }
        composable(BottomBar.Trips.route) { TripsScreen(locationRepository = LocationRepository()) }
        composable(BottomBar.Settings.route) { SettingScreen(navController, themeViewModel = themeViewModel) }
        composable("units"){ UnitScreen(navController,unitViewModel)}
    }
}
