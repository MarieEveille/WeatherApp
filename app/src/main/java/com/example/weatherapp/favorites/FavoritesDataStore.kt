package com.example.weatherapp.favorites

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.favoritesDataStore by preferencesDataStore(name = "favorites")

object FavoritesDataStore {
    private val FAVORITES_KEY = stringPreferencesKey("favorite_cities")

    suspend fun saveFavorites(context: Context, favoriteCities: List<FavoriteCity>) {
        val json = Gson().toJson(favoriteCities)
        context.favoritesDataStore.edit { preferences ->
            preferences[FAVORITES_KEY] = json
        }
    }

    fun getFavorites(context: Context): Flow<List<FavoriteCity>> {
        return context.favoritesDataStore.data.map { preferences ->
            val json = preferences[FAVORITES_KEY] ?: "[]"
            val type = object : TypeToken<List<FavoriteCity>>() {}.type
            Gson().fromJson(json, type) ?: emptyList()
        }
    }
}
