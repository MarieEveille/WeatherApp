package com.example.weatherapp.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.geocoding.City
import com.example.weatherapp.geocoding.GeocodingRetrofitInstance
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

}
