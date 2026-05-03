package com.example.gamewise.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gamewise.ui.theme.GameWisePurple

data class TimeLog(val activity: String, val time: String)

@Composable
fun TimeSpentScreen() {
    val logs = listOf(
        TimeLog("Reading", "2h 30m"),
        TimeLog("Working Out", "1h 15m"),
        TimeLog("Learning Kotlin", "3h 45m"),
        TimeLog("Researching", "1h 00m")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(logs) { log ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(log.activity, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    Text(log.time, color = GameWisePurple)
                }
                HorizontalDivider()
            }
        }
    }
}
