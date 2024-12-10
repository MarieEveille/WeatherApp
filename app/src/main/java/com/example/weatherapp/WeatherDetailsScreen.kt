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
import androidx.compose.material3.CircularProgressIndicator
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
import kotlinx.coroutines.launch


@Composable
fun WeatherDetailsScreen(cityName: String, latitude: Double, longitude: Double) {
    var weatherData by remember { mutableStateOf<WeatherResponse?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(latitude, longitude) {
        scope.launch {
            try {
                errorMessage = null
                weatherData = WeatherRetrofitInstance.api.getWeather(latitude, longitude)
            } catch (e: Exception) {
                Log.e("WeatherApp", "Erreur lors de l'appel API : ${e.message}")
                errorMessage = "Erreur : impossible de récupérer les données météo."
            }
        }
    }

    Scaffold { contentPadding ->
        if (errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(errorMessage ?: "Erreur inconnue.")
            }
        } else if (weatherData != null) {
            val current = weatherData!!.current

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Météo pour $cityName",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text("Température actuelle : ${current.temperature_2m}°C")
                val condition = when (current.weather_code) {
                    0 -> "Ensoleillé"
                    1 -> "Nuageux"
                    2 -> "Pluie"
                    else -> "Conditions inconnues"
                }
                Text("Conditions : $condition")
                Text("Température minimale : ${current.temperature_2m_min}°C")
                Text("Température maximale : ${current.temperature_2m_max}°C")
                Text("Vitesse du vent : ${current.wind_speed_10m} m/s")

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    if (!weatherData!!.hourly.temperature_2m.isNullOrEmpty()) {
                        items(weatherData!!.hourly.temperature_2m.take(24)) { temp ->
                            Text("Température à ${temp}°C")
                        }
                    } else {
                        item { Text("Aucune prévision horaire disponible.") }
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
