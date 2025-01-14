package com.example.weatherapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.weatherapp.favorites.FavoritesDataStore
import com.example.weatherapp.weather.WeatherViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    onRequestLocation: () -> Unit,
    viewModel: WeatherViewModel = viewModel()
) {
    var searchText by rememberSaveable { mutableStateOf("") }
    var showFavorites by rememberSaveable { mutableStateOf(true) }
    var searchTriggered by rememberSaveable { mutableStateOf(false) }

    val cities by viewModel.cities.collectAsState()

    val context = LocalContext.current
    val favoritesFlow = remember { FavoritesDataStore.getFavorites(context) }
    val favorites by favoritesFlow.collectAsState(initial = emptyList())

    val focusManager = LocalFocusManager.current

    DisposableEffect(Unit) {
        onDispose {
            // Remettre à zéro quand on quitte l'écran
            searchText = ""
            showFavorites = true
            searchTriggered = false
        }
    }

    Scaffold { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .clickable {
                    focusManager.clearFocus()
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text("Rechercher une ville") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            if (!searchTriggered) {
                                showFavorites = !focusState.isFocused
                            }
                        },
                    singleLine = true,
                    trailingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Rechercher",
                            modifier = Modifier.clickable {
                                if (searchText.isNotBlank()) {
                                    viewModel.searchCity(searchText)
                                    searchTriggered = true
                                    showFavorites = false
                                    focusManager.clearFocus()
                                }
                            }
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        if (searchText.isNotBlank()) {
                            viewModel.searchCity(searchText)
                            searchTriggered = true
                            showFavorites = false
                            focusManager.clearFocus()
                        }
                    })
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onRequestLocation() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Utiliser ma géolocalisation")
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (showFavorites && !searchTriggered && favorites.isNotEmpty()) {
                    Text(
                        text = "Favoris",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    LazyColumn {
                        items(favorites) { favoriteCity ->
                            Text(
                                text = favoriteCity.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate(
                                            "details/${favoriteCity.name}/${favoriteCity.latitude}/${favoriteCity.longitude}"
                                        )
                                    }
                                    .padding(8.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (searchTriggered) {
                    Text(
                        text = "Résultats de recherche",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    LazyColumn {
                        items(cities) { city ->
                            Text(
                                text = "${city.name}, ${city.country}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate(
                                            "details/${city.name}/${city.latitude}/${city.longitude}"
                                        )
                                    }
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
