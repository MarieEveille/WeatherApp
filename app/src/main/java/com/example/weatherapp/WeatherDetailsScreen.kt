package com.example.weatherapp

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer // Ajout de l'import pour Spacer
import androidx.compose.foundation.layout.Box // Ajout de l'import pour Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme // Ajout de l'import pour MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun WeatherDetailsScreen(cityName: String, latitude: Double, longitude: Double) {
    var weatherData by remember { mutableStateOf<WeatherResponse?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    var isFavorite by remember { mutableStateOf(FavoritesManager.isFavorite(cityName)) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(cityName, latitude, longitude) {
        scope.launch {
            try {
                errorMessage = null
                weatherData = WeatherCacheManager.getWeatherData(context, cityName, latitude, longitude)
            } catch (e: Exception) {
                Log.e("WeatherApp", "Erreur lors de la récupération des données : ${e.message}")
                errorMessage = "Erreur : impossible de récupérer les données météo."
            }
        }
    }

    Scaffold { contentPadding ->
        when {
            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(errorMessage ?: "Erreur inconnue.")
                }
            }

            weatherData != null -> {
                val current = weatherData!!.current
                val daily = weatherData!!.daily

                // On utilise LazyColumn pour tout le contenu
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                        .padding(16.dp)
                ) {
                    // Bloc "header" (texte, favoris, infos courantes)
                    item {
                        Text(
                            text = "Météo pour $cityName",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        IconToggleButton(
                            checked = isFavorite,
                            onCheckedChange = {
                                isFavorite = it
                                if (it) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        FavoritesManager.addFavorite(context, cityName, latitude, longitude)
                                    }
                                } else {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        FavoritesManager.removeFavorite(context, cityName)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.Favorite,
                                contentDescription = if (isFavorite) "Retirer des favoris" else "Ajouter aux favoris"
                            )
                        }

                        Text("Température actuelle : ${current.temperature_2m}°C")
                        val condition = when (current.weather_code) {
                            0 -> "Ensoleillé"
                            1 -> "Nuageux"
                            2 -> "Pluie"
                            else -> "Conditions inconnues"
                        }
                        Text("Conditions : $condition")
                        Text("Température minimale : ${daily.temperature_2m_min}°C")
                        Text("Température maximale : ${daily.temperature_2m_max}°C")
                        Text("Vitesse du vent : ${current.wind_speed_10m} m/s")
                        Text("UV max : ${daily.uv_index_max}")
                        Text("Lever du soleil : ${daily.sunrise}")
                        Text("Coucher du soleil : ${daily.sunset}")

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Bloc "horaire" (24 premiers relevés)
                    if (!weatherData!!.hourly.temperature_2m.isNullOrEmpty()) {
                        items(weatherData!!.hourly.temperature_2m.take(24)) { temp ->
                            Text("Température à $temp°C")
                        }
                    } else {
                        item {
                            Text("Aucune prévision horaire disponible.")
                        }
                    }
                }
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
