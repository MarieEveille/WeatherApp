package com.example.weatherapp

data class CachedWeather(
    val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val weatherData: WeatherResponse,
    val timestamp: Long // Pour savoir quand les données ont été mises en cache
)
