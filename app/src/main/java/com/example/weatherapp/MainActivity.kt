package com.example.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.NavController
import androidx.navigation.NavHostController

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var navController: NavHostController

    // Nouveau : on utilise un « launcher » pour la demande de permission
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission accordée : on peut récupérer la géolocalisation
                getUserLocation()
            } else {
                // Permission refusée
                Toast.makeText(this, "Permission refusée", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        lifecycleScope.launch {
            FavoritesManager.initializeFavorites(this@MainActivity)
        }

        setContent {
            WeatherAppTheme {
                val localNavController = rememberNavController()
                // On l’assigne à la variable de classe pour l’utiliser en-dehors
                navController = localNavController


                NavHost(
                    navController = localNavController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        // On passe la fonction qui va demander la localisation
                        HomeScreen(
                            navController = navController,
                            onRequestLocation = {
                                requestUserLocation()
                            }
                        )
                    }
                    composable("details/{cityName}/{latitude}/{longitude}") { backStackEntry ->
                        val cityName = backStackEntry.arguments?.getString("cityName") ?: ""
                        val latitude =
                            backStackEntry.arguments?.getString("latitude")?.toDouble() ?: 0.0
                        val longitude =
                            backStackEntry.arguments?.getString("longitude")?.toDouble() ?: 0.0

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

    /**
     * Vérifie si la permission est déjà accordée ou non.
     * Si elle ne l’est pas, on lance la demande via `requestPermissionLauncher`.
     */
    private fun requestUserLocation() {
        val permissionStatus = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            // Permission déjà accordée
            getUserLocation()
        } else {
            // Lancer la demande de permission
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    /**
     * Récupère la dernière position connue (requiert la permission accordée).
     */
    @SuppressLint("MissingPermission")
    private fun getUserLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val lat = location.latitude
                val lon = location.longitude
                Log.d("MainActivity", "Latitude: $lat, Longitude: $lon")
                lifecycleScope.launch {
                    try {
                        val weatherResponse = WeatherCacheManager.getWeatherData(
                            context = this@MainActivity,
                            cityName = "cityName", // Nom fictif ou provenant d'un reverse geocoding
                            latitude = lat,
                            longitude = lon
                        )
                        navController.navigate("details/Localisation/$lat/$lon")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                Toast.makeText(this, "Impossible d’obtenir la localisation", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
