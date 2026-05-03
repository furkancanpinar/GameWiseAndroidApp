package com.example.gamewise.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gamewise.ui.theme.GameWisePurple

@Composable
fun GameScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Gaming Dashboard",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = GameWisePurple
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Coming Soon: Track and manage your games here!",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
