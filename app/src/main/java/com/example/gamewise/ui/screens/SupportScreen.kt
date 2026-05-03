package com.example.gamewise.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gamewise.ui.theme.GameWisePurple

data class SupportTopic(val title: String)

@Composable
fun SupportScreen() {
    val topics = listOf(
        SupportTopic("Account Issues"),
        SupportTopic("Technical Support"),
        SupportTopic("Billing & Subscriptions"),
        SupportTopic("General Feedback")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(topics) { topic ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    ListItem(headlineContent = { Text(topic.title) })
                }
            }
        }
    }
}
