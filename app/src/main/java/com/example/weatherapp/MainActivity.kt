package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.weatherapp.ui.theme.WeatherAppTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.lifecycleScope

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            FavoritesManager.initializeFavorites(this@MainActivity)
        }
        setContent {
            WeatherAppTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        HomeScreen(navController)
                    }
                    composable("details/{cityName}/{latitude}/{longitude}") { backStackEntry ->
                        val cityName = backStackEntry.arguments?.getString("cityName") ?: ""
                        val latitude = backStackEntry.arguments?.getString("latitude")?.toDouble() ?: 0.0
                        val longitude = backStackEntry.arguments?.getString("longitude")?.toDouble() ?: 0.0

                        WeatherDetailsScreen(
                            cityName = cityName,
                            latitude = latitude,
                            longitude = longitude
                        )
                    }
                }
            }
        }
    }
}

