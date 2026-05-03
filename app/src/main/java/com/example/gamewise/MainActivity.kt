package com.example.gamewise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gamewise.ui.GameWiseNavGraph
import com.example.gamewise.ui.Screen
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
                MainContainer()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Define which screens should show the top bar and drawer
    val authenticatedScreens = listOf(
        Screen.Home.route,
        Screen.Ideas.route,
        Screen.Assessment.route,
        Screen.TimeSpent.route,
        Screen.AiAssistant.route,
        Screen.Support.route,
        Screen.Settings.route
    )

    val showDrawer = currentRoute in authenticatedScreens

    if (showDrawer) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = GameWiseCoral,
                    drawerContentColor = Color.Black
                ) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "GameWise Menu",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black
                    )
                    
                    val menuItems = listOf(
                        Triple("Home", Icons.Default.Home, Screen.Home.route),
                        Triple("Ideas", Icons.Default.Lightbulb, Screen.Ideas.route),
                        Triple("Assessment", Icons.Default.Checklist, Screen.Assessment.route),
                        Triple("Time Spent", Icons.Default.Timer, Screen.TimeSpent.route),
                        Triple("GameWAi", Icons.Default.SmartToy, Screen.AiAssistant.route),
                        Triple("Support", Icons.Default.SupportAgent, Screen.Support.route),
                        Triple("Settings", Icons.Default.Settings, Screen.Settings.route)
                    )

                    menuItems.forEach { (title, icon, route) ->
                        NavigationDrawerItem(
                            icon = { Icon(icon, contentDescription = null) },
                            label = { Text(title) },
                            selected = currentRoute == route,
                            onClick = {
                                navController.navigate(route) {
                                    popUpTo(Screen.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
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
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = GameWisePurple)
                    )
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    GameWiseNavGraph(navController = navController)
                }
            }
        }
    } else {
        // Show login/signup without drawer
        GameWiseNavGraph(navController = navController)
    }
}
