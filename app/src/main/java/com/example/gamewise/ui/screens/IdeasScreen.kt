package com.example.gamewise.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gamewise.ui.components.SearchBar
import com.example.gamewise.ui.theme.GameWisePurple

data class Idea(val title: String, val content: String)

@Composable
fun IdeasScreen() {
    var searchQuery by remember { mutableStateOf("") }
    val ideas = listOf(
        Idea("Productivity Hacks", "How to stay focused during long sessions."),
        Idea("Creative Writing", "Prompts for your next story."),
        Idea("New Language", "Tips for learning a language quickly."),
        Idea("Health & Fitness", "Stay active while sitting.")
    )

    val filteredIdeas = ideas.filter { it.title.contains(searchQuery, ignoreCase = true) }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(query = searchQuery, onQueryChange = { searchQuery = it }, placeholder = "Search ideas...")

        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(filteredIdeas) { idea ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(idea.title, style = MaterialTheme.typography.titleMedium, color = GameWisePurple)
                        Text(idea.content, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
