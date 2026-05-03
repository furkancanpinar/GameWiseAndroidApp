package com.example.gamewise.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.example.gamewise.ui.Screen
import com.example.gamewise.ui.theme.GameWisePurple

data class SearchResult(
    val title: String,
    val description: String,
    val route: String,
    val icon: ImageVector
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameWiseSearchBar(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    val searchResults = listOf(
        SearchResult("Home Page", "Main landing page - News and updates", Screen.Home.route, Icons.Default.Home),
        SearchResult("GameWAi", "AI learning assistant - Ask questions about games", Screen.AiAssistant.route, Icons.Default.SmartToy),
        SearchResult("Support", "Get help and support - Contact us", Screen.Support.route, Icons.Default.SupportAgent),
        SearchResult("Login", "Sign in to your account - Profile access", Screen.Login.route, Icons.Default.Person),
        SearchResult("Ideas", "Discover new game ideas - Inspiration", Screen.Ideas.route, Icons.Default.Lightbulb),
        SearchResult("Assessment", "Take a gaming assessment - Skills test", Screen.Assessment.route, Icons.Default.Checklist),
        SearchResult("Settings", "App preferences - Notifications and account", Screen.Settings.route, Icons.Default.Settings),
        SearchResult("Time Spent", "Track your gaming time - Usage statistics", Screen.TimeSpent.route, Icons.Default.Timer),

        SearchResult("Game Tips", "Find tips in GameWAi", Screen.AiAssistant.route, Icons.Default.TipsAndUpdates),
        SearchResult("Account Safety", "Manage in Settings", Screen.Settings.route, Icons.Default.Security),
        SearchResult("Contact Us", "Reach out in Support", Screen.Support.route, Icons.Default.Email)
    )

    val filteredResults = if (query.isEmpty()) {
        emptyList()
    } else {
        searchResults.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.description.contains(query, ignoreCase = true)
        }
    }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = query,
            onValueChange = { 
                query = it
                active = it.isNotEmpty()
            },
            modifier = Modifier
                .width(300.dp)
                .height(50.dp),
            placeholder = { Text("Search...", color = Color.Gray) },
            trailingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            shape = RoundedCornerShape(25.dp),
            singleLine = true
        )

        if (active && filteredResults.isNotEmpty()) {
            Popup(
                alignment = Alignment.TopStart,
                onDismissRequest = { active = false }
            ) {
                Card(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .width(300.dp)
                        .heightIn(max = 400.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    LazyColumn {
                        items(filteredResults) { result ->
                            SearchResultItem(result) {
                                query = ""
                                active = false
                                onNavigate(result.route)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(
    result: SearchResult,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = result.icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = result.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = GameWisePurple
            )
            Text(
                text = result.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}
