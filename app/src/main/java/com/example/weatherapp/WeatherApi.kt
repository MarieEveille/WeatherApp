package com.example.weatherapp

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    // Fonction pour rechercher une ville (Géocodage)
    @GET("v1/search")
    suspend fun searchCity(@Query("name") cityName: String): GeocodingResponse

    // Fonction pour obtenir la météo (Prévisions)
    @GET("forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,apparent_temperature,wind_speed_10m,weather_code",  // Données actuelles
        @Query("hourly") hourly: String = "temperature_2m,relative_humidity_2m,apparent_temperature,rain,wind_speed_10m", // Données horaires
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min,sunrise,sunset,uv_index_max",  // Données journalières
        @Query("timezone") timezone: String = "Europe/Berlin",  // Fuseau horaire
        @Query("forecast_days") forecastDays: Int = 1,  // Nombre de jours de prévisions
        @Query("models") model: String = "meteofrance_seamless"  // Modèle
    ): WeatherResponse
}

