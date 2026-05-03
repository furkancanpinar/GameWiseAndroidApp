package com.example.gamewise.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gamewise.ui.components.SearchBar
import com.example.gamewise.ui.theme.GameWisePurple

data class HomeFeature(val title: String, val icon: ImageVector, val desc: String)

@Composable
fun HomeScreen() {
    var searchQuery by remember { mutableStateOf("") }
    val features = listOf(
        HomeFeature("Ideas", Icons.Default.Lightbulb, "Explore New Ideas"),
        HomeFeature("Assessment", Icons.Default.Checklist, "Take a Self Test"),
        HomeFeature("AI Assistant", Icons.Default.SmartToy, "Chat with GameWAi"),
        HomeFeature("Support", Icons.Default.SupportAgent, "Get Help Now")
    )

    val filteredFeatures = features.filter {
        it.title.contains(searchQuery, ignoreCase = true) || it.desc.contains(searchQuery, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholder = "Search dashboard..."
        )

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Welcome to GameWise",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = GameWisePurple
            )
            Text(
                "Your ultimate companion",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredFeatures) { feature ->
                    FeatureCard(feature)
                }
            }
        }
    }
}

@Composable
fun FeatureCard(feature: HomeFeature) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(feature.icon, contentDescription = null, tint = GameWisePurple)
            Spacer(modifier = Modifier.height(8.dp))
            Text(feature.title, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(feature.desc, style = MaterialTheme.typography.bodySmall, color = Color.Black.copy(alpha = 0.7f))
        }
    }
}
