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

data class ChatMessage(val user: String, val message: String)

@Composable
fun AiAssistantScreen() {
    var searchQuery by remember { mutableStateOf("") }
    val chatHistory = listOf(
        ChatMessage("User", "How do I start a new project?"),
        ChatMessage("GameWAi", "You can start by defining your goals first."),
        ChatMessage("User", "What's the best way to learn?"),
        ChatMessage("GameWAi", "Practice and consistency are key.")
    )

    val filteredChat = chatHistory.filter { it.message.contains(searchQuery, ignoreCase = true) }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(query = searchQuery, onQueryChange = { searchQuery = it }, placeholder = "Search chat history...")

        LazyColumn(modifier = Modifier.padding(16.dp).weight(1f)) {
            items(filteredChat) { chat ->
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(chat.user, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = if(chat.user == "GameWAi") GameWisePurple else MaterialTheme.colorScheme.primary)
                    Text(chat.message)
                }
            }
        }
        
        Row(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Ask GameWAi...") }, modifier = Modifier.weight(1f))
            Button(onClick = {}, modifier = Modifier.padding(start = 8.dp)) { Text("Send") }
        }
    }
}
