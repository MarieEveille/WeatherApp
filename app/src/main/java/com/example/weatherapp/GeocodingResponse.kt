package com.example.weatherapp

data class GeocodingResponse(
    val results: List<City>?
)

data class City(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?
)
