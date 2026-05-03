package com.example.gamewise.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gamewise.ui.theme.GameWisePurple

data class Question(val text: String)

@Composable
fun AssessmentScreen() {
    val questions = listOf(
        Question("Do you take frequent breaks?"),
        Question("Do you set daily goals?"),
        Question("Do you feel rested?"),
        Question("Are you drinking enough water?")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(questions) { q ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                    Text(q.text, modifier = Modifier.weight(1f))
                    Checkbox(checked = false, onCheckedChange = {})
                }
                Divider()
            }
        }
    }
}
