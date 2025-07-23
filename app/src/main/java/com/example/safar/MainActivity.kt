package com.example.safar

import SafarTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.safar.Screens.AppNavigation
import com.example.safar.Screens.BottomNavBar
import com.example.safar.viewModels.ThemeViewModel

class MainActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SafarTheme(
                darkTheme = themeViewModel.isDarkTheme
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    Scaffold(
                        bottomBar = {
                            BottomNavBar(navController)
                        }
                    ) {
                        paddingValues ->
                        AppNavigation(
                            navController = navController,
                            innerPadding = paddingValues,
                            themeViewModel = themeViewModel
                        )
                    }
                }
            }
        }
    }
}

