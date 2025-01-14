package com.example.weatherapp.weather.cache

import com.example.weatherapp.weather.WeatherResponse

data class CachedWeather(
    val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val weatherData: WeatherResponse,
    val timestamp: Long
)
