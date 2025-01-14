package com.example.weatherapp.weather.cache

import android.content.Context
import android.util.Log
import com.example.weatherapp.weather.WeatherResponse
import com.example.weatherapp.weather.WeatherRetrofitInstance
import kotlinx.coroutines.flow.first

object WeatherCacheManager {
    private const val CACHE_EXPIRATION = 60 * 60 * 1000

    suspend fun getWeatherData(
        context: Context,
        cityName: String,
        latitude: Double,
        longitude: Double
    ): WeatherResponse {
        val cachedWeathers = WeatherCacheDataStore.getCachedWeathers(context).first()
        Log.d("WeatherCacheManager", "Données en cache : $cachedWeathers")

        val cachedWeather = cachedWeathers.find { it.cityName == cityName }
        Log.d("WeatherCacheManager", "Données en cache pour $cityName : $cachedWeather")

        if (cachedWeather != null
        ) {
            Log.d("WeatherCacheManager", "Données récupérées depuis le cache pour $cityName.")
            return cachedWeather.weatherData
        }

        val newWeatherData = WeatherRetrofitInstance.api.getWeather(latitude, longitude)
        val newCachedWeather = CachedWeather(
            cityName = cityName,
            latitude = latitude,
            longitude = longitude,
            weatherData = newWeatherData,
            timestamp = System.currentTimeMillis()
        )

        val updatedCache = cachedWeathers.filter { it.cityName != cityName } + newCachedWeather
        WeatherCacheDataStore.saveWeathers(context, updatedCache)

        Log.d("WeatherCacheManager", "Cache mis à jour avec les données pour $cityName.")
        return newWeatherData
    }
}
