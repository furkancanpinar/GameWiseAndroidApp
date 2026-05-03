package com.example.gamewise.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gamewise.ui.components.SearchBar

data class SettingItem(val name: String)

@Composable
fun SettingsScreen() {
    var searchQuery by remember { mutableStateOf("") }
    val settings = listOf(
        SettingItem("Profile Settings"),
        SettingItem("Notification Preferences"),
        SettingItem("Privacy Control"),
        SettingItem("Theme (Light/Dark)")
    )

    val filteredSettings = settings.filter { it.name.contains(searchQuery, ignoreCase = true) }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(query = searchQuery, onQueryChange = { searchQuery = it }, placeholder = "Search settings...")

        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(filteredSettings) { setting ->
                ListItem(headlineContent = { Text(setting.name) })
                HorizontalDivider()
            }
        }
    }
}
