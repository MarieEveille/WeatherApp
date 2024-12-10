package com.example.weatherapp

data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val hourly: HourlyWeather
)

data class HourlyWeather(
    val temperature_2m: List<Double>
)
