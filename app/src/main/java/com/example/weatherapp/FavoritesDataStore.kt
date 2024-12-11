package com.example.weatherapp

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension pour créer DataStore
val Context.favoritesDataStore by preferencesDataStore(name = "favorites")

object FavoritesDataStore {
    private val FAVORITES_KEY = stringPreferencesKey("favorite_cities")

    // Sauvegarder la liste des favoris
    suspend fun saveFavorites(context: Context, favoriteCities: List<FavoriteCity>) {
        val json = Gson().toJson(favoriteCities) // Sérialisation en JSON
        context.favoritesDataStore.edit { preferences ->
            preferences[FAVORITES_KEY] = json
        }
    }

    // Charger les favoris
    fun getFavorites(context: Context): Flow<List<FavoriteCity>> {
        return context.favoritesDataStore.data.map { preferences ->
            val json = preferences[FAVORITES_KEY] ?: "[]"
            val type = object : TypeToken<List<FavoriteCity>>() {}.type
            Gson().fromJson(json, type) ?: emptyList()
        }
    }
}
