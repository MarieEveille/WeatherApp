package com.example.weatherapp

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object WeatherCacheManager {
    private const val CACHE_EXPIRATION = 60 * 60 * 1000 // 1 heure en millisecondes

    // Obtenir les données météo pour une ville
    suspend fun getWeatherData(
        context: Context,
        cityName: String,
        latitude: Double,
        longitude: Double
    ): WeatherResponse {
        val cachedWeathers = WeatherCacheDataStore.getCachedWeathers(context).first()
        Log.d("WeatherCacheManager", "Données en cache : $cachedWeathers")

        // Rechercher la ville dans le cache
        val cachedWeather = cachedWeathers.find { it.cityName == cityName }
        Log.d("WeatherCacheManager", "Données en cache pour $cityName : $cachedWeather")

        // Si les données en cache sont valides, les retourner
        if (cachedWeather != null
        ) {
            Log.d("WeatherCacheManager", "Données récupérées depuis le cache pour $cityName.")
            return cachedWeather.weatherData
        }

        // Sinon, appeler l'API et mettre à jour le cache
        val newWeatherData = WeatherRetrofitInstance.api.getWeather(latitude, longitude)
        val newCachedWeather = CachedWeather(
            cityName = cityName,
            latitude = latitude,
            longitude = longitude,
            weatherData = newWeatherData,
            timestamp = System.currentTimeMillis()
        )

        // Mettre à jour ou ajouter la ville dans le tableau
        val updatedCache = cachedWeathers.filter { it.cityName != cityName } + newCachedWeather
        WeatherCacheDataStore.saveWeathers(context, updatedCache)

        Log.d("WeatherCacheManager", "Cache mis à jour avec les données pour $cityName.")
        return newWeatherData
    }
}
