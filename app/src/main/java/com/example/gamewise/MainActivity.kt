package com.example.gamewise

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.gamewise.data.auth.AuthRepository
import com.example.gamewise.ui.components.GameWiseSearchBar
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
    val authRepository = remember { AuthRepository() }
    val user by authRepository.observeUser().collectAsState(initial = authRepository.getCurrentUser())
    val context = androidx.compose.ui.platform.LocalContext.current
    
    // Logic to handle "Remember Me"
    LaunchedEffect(Unit) {
        val sharedPrefs = context.getSharedPreferences("gamewise_prefs", Context.MODE_PRIVATE)
        val rememberMe = sharedPrefs.getBoolean("remember_me", false)
        if (!rememberMe && authRepository.getCurrentUser() != null) {
            authRepository.logout()
        }
    }

    var showProfileMenu by remember { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Define which screens should show the top bar and drawer
    val authenticatedScreens = listOf(
        Screen.Home.route,
        Screen.Game.route,
        Screen.Ideas.route,
        Screen.Assessment.route,
        Screen.TimeSpent.route,
        Screen.AiAssistant.route,
        Screen.Support.route,
        Screen.Settings.route,
        Screen.Profile.route
    )

    val showTopBar = currentRoute in authenticatedScreens || currentRoute == Screen.Login.route || currentRoute == Screen.SignUp.route
    val showDrawer = currentRoute in authenticatedScreens

    val currentTitle = when (currentRoute) {
        Screen.Home.route -> "HOME"
        Screen.Game.route -> "GAME"
        Screen.Ideas.route -> "IDEAS"
        Screen.Assessment.route -> "ASSESSMENT"
        Screen.TimeSpent.route -> "TIME SPENT"
        Screen.AiAssistant.route -> "GAMEWAI"
        Screen.Support.route -> "SUPPORT"
        Screen.Settings.route -> "SETTINGS"
        Screen.Profile.route -> "PROFILE"
        Screen.Login.route -> "LOGIN"
        Screen.SignUp.route -> "SIGN UP"
        else -> "GAMEWISE"
    }

    if (showTopBar) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = showDrawer,
            drawerContent = {
                if (showDrawer) {
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
                            Triple("Gaming", Icons.Default.SportsEsports, Screen.Game.route),
                            Triple("Ideas", Icons.Default.Lightbulb, Screen.Ideas.route),
                            Triple("Assessment", Icons.Default.Checklist, Screen.Assessment.route),
                            Triple("Time Spent", Icons.Default.Timer, Screen.TimeSpent.route),
                            Triple("GameWAi", Icons.Default.SmartToy, Screen.AiAssistant.route),
                            Triple("Support", Icons.Default.SupportAgent, Screen.Support.route),
                            Triple("Settings", Icons.Default.Settings, Screen.Settings.route),
                            Triple("Profile", Icons.Default.Person, Screen.Profile.route)
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
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Logo
                                Icon(
                                    painter = painterResource(id = R.drawable.alien),
                                    contentDescription = "Logo",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(32.dp)
                                )
                                Text(
                                    text = currentTitle,
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Spacer(modifier = Modifier.weight(1f))
                                
                                if (showDrawer) {
                                    GameWiseSearchBar(
                                        onNavigate = { route ->
                                            navController.navigate(route) {
                                                popUpTo(Screen.Home.route) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )

                                    Spacer(modifier = Modifier.weight(1f))

                                    Box {
                                        val currentUser = user
                                        IconButton(onClick = { showProfileMenu = true }) {
                                            if (currentUser?.photoUrl != null) {
                                                // Use a key to force refresh
                                                val imageKey = remember(currentUser.photoUrl) {
                                                    "${currentUser.photoUrl}?t=${System.currentTimeMillis()}"
                                                }
                                                AsyncImage(
                                                    model = imageKey,
                                                    contentDescription = "Profile",
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .clip(androidx.compose.foundation.shape.CircleShape),
                                                    contentScale = ContentScale.Crop
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = Icons.Default.AccountCircle,
                                                    contentDescription = "Profile",
                                                    tint = Color.Black,
                                                    modifier = Modifier.size(32.dp)
                                                )
                                            }
                                        }
                                        DropdownMenu(
                                            expanded = showProfileMenu,
                                            onDismissRequest = { showProfileMenu = false }
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text("Profile") },
                                                onClick = {
                                                    showProfileMenu = false
                                                    navController.navigate(Screen.Profile.route)
                                                },
                                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("Sign Out") },
                                                onClick = {
                                                    showProfileMenu = false
                                                    authRepository.logout()
                                                    navController.navigate(Screen.Login.route) {
                                                        popUpTo(0) { inclusive = true }
                                                    }
                                                },
                                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) }
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        navigationIcon = {
                            if (showDrawer) {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = GameWisePurple)
                    )
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    GameWiseNavGraph(
                        navController = navController,
                        isUserLoggedIn = user != null
                    )
                }
            }
        }
    } else {
        // Show login/signup without drawer
        GameWiseNavGraph(
            navController = navController,
            isUserLoggedIn = user != null
        )
    }
}
