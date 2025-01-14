package com.example.weatherapp.weather.cache

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.weatherCacheDataStore by preferencesDataStore(name = "weather_cache")

object WeatherCacheDataStore {
    private val WEATHER_CACHE_KEY = stringPreferencesKey("cached_weather")

    suspend fun saveWeathers(context: Context, cachedWeathers: List<CachedWeather>) {
        val json = Gson().toJson(cachedWeathers)
        Log.d("WeatherCacheDataStore", "Données sauvegardées : $json")
        context.weatherCacheDataStore.edit { preferences ->
            preferences[WEATHER_CACHE_KEY] = json
        }
    }

    fun getCachedWeathers(context: Context): Flow<List<CachedWeather>> {
        return context.weatherCacheDataStore.data.map { preferences ->
            val json = preferences[WEATHER_CACHE_KEY] ?: "[]"
            Log.d("WeatherCacheDataStore", "Données chargées : $json")
            val type = object : TypeToken<List<CachedWeather>>() {}.type
            Log.d("WeatherCacheDataStore", "Type : $type")
            Gson().fromJson(json, type) ?: emptyList()

        }
    }
}
