package com.example.safar.Screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState



@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        BottomBar.Home, BottomBar.Trips, BottomBar.Statistics, BottomBar.Settings
    )
    NavigationBar {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route
        items.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route){
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    }
                },
                icon = {
                    when (screen) {
                        is BottomBar.Home -> Icon(Icons.Default.Home, "Home")
                        is BottomBar.Trips -> Icon(Icons.AutoMirrored.Filled.List, "Trips")
                        is BottomBar.Statistics -> Icon(Icons.Default.DateRange, "Stats")
                        is BottomBar.Settings -> Icon(Icons.Default.Settings, "Settings")
                    }
                },
                label = { Text(screen.route.replaceFirstChar { it.uppercase() }) }
            )
        }
    }
}
