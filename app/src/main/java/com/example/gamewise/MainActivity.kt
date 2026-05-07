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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
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

    val showDrawer = currentRoute in authenticatedScreens
    val showTopBar = showDrawer || currentRoute == Screen.Login.route || currentRoute == Screen.SignUp.route

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

    // Logic to wrap in Drawer only when needed
    @Composable
    fun ContentWrapper(content: @Composable (PaddingValues) -> Unit) {
        if (showDrawer) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                gesturesEnabled = true,
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
                                    if (currentRoute != route) {
                                        navController.navigate(route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
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
                            title = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.alien),
                                        contentDescription = "Logo",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            },
                            actions = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    GameWiseSearchBar(
                                        modifier = Modifier.width(220.dp),
                                        onNavigate = { route ->
                                            navController.navigate(route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                    Box {
                                        val currentUser = user
                                        IconButton(onClick = { showProfileMenu = true }) {
                                            if (currentUser?.photoUrl != null) {
                                                AsyncImage(
                                                    model = ImageRequest.Builder(LocalContext.current)
                                                        .data(currentUser.photoUrl)
                                                        .crossfade(true)
                                                        .memoryCacheKey(currentUser.photoUrl.toString())
                                                        .diskCacheKey(currentUser.photoUrl.toString())
                                                        .build(),
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
                                                    tint = Color.White,
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
                            },
                            navigationIcon = {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = GameWisePurple)
                        )
                    },
                    content = content
                )
            }
        } else if (showTopBar) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.alien),
                                    contentDescription = "Logo",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = GameWisePurple)
                    )
                },
                content = content
            )
        } else {
            content(PaddingValues(0.dp))
        }
    }

    ContentWrapper { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            GameWiseNavGraph(
                navController = navController,
                startDestination = if (user != null) Screen.Home.route else Screen.Login.route
            )
        }
    }
}
