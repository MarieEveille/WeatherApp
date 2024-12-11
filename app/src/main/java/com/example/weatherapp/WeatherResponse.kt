package com.example.weatherapp

data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val current: CurrentWeather,
    val hourly: HourlyWeather,
    val daily: DailyWeather
)

data class DailyWeather(
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>,
    val sunrise : List<String>,
    val sunset : List<String>,
    val uv_index_max : List<Double>

)

data class CurrentWeather(
    val temperature_2m: Double,
    val weather_code: Int,
    val wind_speed_10m: Double,
    val temperature_2m_max: Double,
    val temperature_2m_min: Double
)

data class HourlyWeather(
    val temperature_2m: List<Double>,
    val wind_speed_10m: List<Double>,
    val weather_code: List<Int>
)
