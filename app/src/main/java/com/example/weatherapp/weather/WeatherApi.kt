package com.example.weatherapp.weather

import com.example.weatherapp.geocoding.GeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("v1/search")
    suspend fun searchCity(@Query("name") cityName: String): GeocodingResponse


    @GET("forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,apparent_temperature,wind_speed_10m,weather_code",
        @Query("hourly") hourly: String = "temperature_2m,relative_humidity_2m,apparent_temperature,rain,wind_speed_10m",
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min,sunrise,sunset,uv_index_max",
        @Query("timezone") timezone: String = "Europe/Berlin",
        @Query("forecast_days") forecastDays: Int = 1,
        @Query("models") model: String = "meteofrance_seamless"
    ): WeatherResponse

}

