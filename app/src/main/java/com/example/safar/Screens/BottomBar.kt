package com.example.safar.Screens

sealed class BottomBar(val route: String) {
    object Home : BottomBar("home")
    object Trips : BottomBar("trips")
    object Settings : BottomBar("settings")
}
