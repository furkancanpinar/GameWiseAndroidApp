package com.example.gamewise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.unit.sp
import com.example.gamewise.ui.theme.GameWiseCoral
import com.example.gamewise.ui.theme.GameWisePurple
import com.example.gamewise.ui.theme.GameWiseTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GameWiseTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItem by remember { mutableStateOf("Home") }

    val menuItems = listOf(
        MenuItem("Home", Icons.Default.Home),
        MenuItem("Games", Icons.Default.PlayArrow),
        MenuItem("Ideas", Icons.Default.Lightbulb),
        MenuItem("Assessment", Icons.Default.Checklist),
        MenuItem("Time Spent", Icons.Default.Timer),
        MenuItem("GameWAi", Icons.Default.SmartToy),
        MenuItem("Support", Icons.Default.SupportAgent),
        MenuItem("Settings", Icons.Default.Settings)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = GameWiseCoral, // Side menu: #ff9696
                drawerContentColor = Color.Black      // Text in side menu
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "GameWise Menu",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black
                )
                menuItems.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(item.title) },
                        selected = selectedItem == item.title,
                        onClick = {
                            selectedItem = item.title
                            scope.launch { drawerState.close() }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color.Transparent,
                            selectedContainerColor = Color.White.copy(alpha = 0.3f),
                            unselectedIconColor = Color.Black,
                            selectedIconColor = Color.Black,
                            unselectedTextColor = Color.Black,
                            selectedTextColor = Color.Black
                        ),
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("GameWise", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Search logic */ }) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                        }
                        IconButton(onClick = { /* Profile logic */ }) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = GameWisePurple // Top banner: #ad84ff
                    )
                )
            }
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {
                when (selectedItem) {
                    "Home" -> DashboardScreen()
                    else -> PlaceholderScreen(selectedItem)
                }
            }
        }
    }
}

@Composable
fun DashboardScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Welcome to GameWise",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = GameWisePurple // Headers: #ad84ff
        )
        Text(
            text = "Your ultimate gaming companion",
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
            item { FeatureCard("Games", Icons.Default.PlayArrow, "Start Playing") }
            item { FeatureCard("Ideas", Icons.Default.Lightbulb, "Explore Ideas") }
            item { FeatureCard("AI Assistant", Icons.Default.SmartToy, "Chat with AI") }
            item { FeatureCard("Stats", Icons.Default.BarChart, "View Progress") }
        }
    }
}

@Composable
fun FeatureCard(title: String, icon: ImageVector, description: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        onClick = { /* Navigate */ },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = GameWisePurple
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                title, 
                fontWeight = FontWeight.Bold,
                color = Color.Black // Text in boxes: Black
            )
            Text(
                description, 
                fontSize = 12.sp, 
                color = Color.Black.copy(alpha = 0.7f) // Text in boxes: Black
            )
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            "$title Content Coming Soon", 
            style = MaterialTheme.typography.titleMedium,
            color = GameWisePurple // Using header color for placeholder text
        )
    }
}

data class MenuItem(val title: String, val icon: ImageVector)
