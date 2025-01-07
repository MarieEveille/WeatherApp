package com.example.weatherapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val _cities = MutableStateFlow<List<City>>(emptyList())
    val cities: StateFlow<List<City>> = _cities

    fun searchCity(cityName: String) {
        viewModelScope.launch {
            try {
                val response = GeocodingRetrofitInstance.api.searchCity(cityName)
                _cities.value = response.results ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val response = WeatherRetrofitInstance.api.getWeather(lat, lon)

            } catch (e: Exception) {
                // Gérer l’erreur
            }
        }
    }
}
