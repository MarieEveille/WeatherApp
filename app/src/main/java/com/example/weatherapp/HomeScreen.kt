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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController, viewModel: WeatherViewModel = viewModel()) {
    var searchText by remember { mutableStateOf("") }
    val cities by viewModel.cities.collectAsState()

    Scaffold { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(16.dp)
        ) {
            // Barre de recherche
            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Rechercher une ville") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Rechercher", modifier = Modifier.clickable {
                        viewModel.searchCity(searchText)
                    })
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Liste des résultats
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
