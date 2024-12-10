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
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val url = "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&model=meteofrance_seamless"
                Log.d("WeatherApp", "URL de la requête : $url") // Afficher l'URL dans les logs

                val response = WeatherRetrofitInstance.api.getWeather(latitude, longitude)
                weatherData = response
            } catch (e: Exception) {
                Log.e("WeatherApp", "Erreur : ${e.message}")
            }
        }
    }

    Scaffold { contentPadding ->
        if (weatherData != null) {
            val hourly = weatherData!!.hourly
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Météo pour $cityName",
                    style = MaterialTheme.typography.titleLarge, // Correction du style, si h5 ne fonctionne pas
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Afficher les informations météo
                Text("Latitude : $latitude")
                Text("Longitude : $longitude")
                Spacer(modifier = Modifier.height(16.dp))

                // Exemple d'affichage de la température
                LazyColumn {
                    items(hourly.temperature_2m.take(24)) { temp ->
                        Text("Température : $temp°C")
                    }
                }
            }
        } else {
            // Écran de chargement
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
