package com.example.gamewise.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class SettingItem(val name: String)

@Composable
fun SettingsScreen() {
    val settings = listOf(
        SettingItem("Profile Settings"),
        SettingItem("Notification Preferences"),
        SettingItem("Privacy Control"),
        SettingItem("Theme (Light/Dark)")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(settings) { setting ->
                ListItem(headlineContent = { Text(setting.name) })
                HorizontalDivider()
            }
        }
    }
}
