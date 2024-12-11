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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.runtime.*
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController, viewModel: WeatherViewModel = viewModel()) {
    var searchText by remember { mutableStateOf("") }
    val cities by viewModel.cities.collectAsState()
    val context = LocalContext.current
    val favoritesFlow = remember { FavoritesDataStore.getFavorites(context) }
    val favorites by favoritesFlow.collectAsState(initial = emptyList())
    var showFavorites by remember { mutableStateOf(true) }
    var searchTriggered by remember { mutableStateOf(false) } // État pour savoir si une recherche est en cours
    val focusManager = LocalFocusManager.current

    Scaffold { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .clickable {
                    focusManager.clearFocus() // Défocaliser la barre de recherche si on clique ailleurs
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Barre de recherche
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text("Rechercher une ville") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            if (!searchTriggered) {
                                showFavorites = !focusState.isFocused // Cache les favoris uniquement si aucune recherche
                            }
                        },
                    singleLine = true,
                    trailingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Rechercher", modifier = Modifier.clickable {
                            if (searchText.isNotBlank()) {
                                viewModel.searchCity(searchText)
                                searchTriggered = true // Déclenche l'affichage des résultats
                                showFavorites = false // Cache les favoris après la recherche
                                focusManager.clearFocus() // Ferme le clavier après recherche
                            }
                        })
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        if (searchText.isNotBlank()) {
                            viewModel.searchCity(searchText)
                            searchTriggered = true // Déclenche l'affichage des résultats
                            showFavorites = false // Cache les favoris après l'action de recherche
                            focusManager.clearFocus() // Ferme le clavier après recherche
                        }
                    })
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Section des favoris
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
                                        // Naviguer vers l'écran de détails météo
                                        navController.navigate("details/${favoriteCity.name}/${favoriteCity.latitude}/${favoriteCity.longitude}")
                                    }
                                    .padding(8.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Liste des résultats
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
                                        // Naviguer vers l'écran de détails météo
                                        navController.navigate("details/${city.name}/${city.latitude}/${city.longitude}")
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
