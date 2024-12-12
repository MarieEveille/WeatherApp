package com.example.weatherapp

import android.content.Context

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object FavoritesManager {
    private val favoriteCities = mutableSetOf<FavoriteCity>()

    suspend fun initializeFavorites(context: Context) {
        // Charger les favoris depuis DataStore
        FavoritesDataStore.getFavorites(context).collect { loadedFavorites ->
            favoriteCities.clear()
            favoriteCities.addAll(loadedFavorites)
        }
    }


    fun addFavorite(context: Context, cityName: String, latitude: Double, longitude: Double) {
        favoriteCities.add(FavoriteCity(cityName, latitude, longitude))
        CoroutineScope(Dispatchers.IO).launch {
            saveFavoritesToCache(context)
        }
    }

    fun removeFavorite(context: Context, cityName: String) {
        favoriteCities.removeIf { it.name == cityName }
        CoroutineScope(Dispatchers.IO).launch {
            saveFavoritesToCache(context)
        }
    }

    fun isFavorite(cityName: String): Boolean {
        return favoriteCities.any { it.name == cityName }
    }

    fun getFavorites(): List<FavoriteCity> {
        return favoriteCities.toList()
    }

    private suspend fun saveFavoritesToCache(context: Context) {
        FavoritesDataStore.saveFavorites(context, getFavorites())
    }
}
