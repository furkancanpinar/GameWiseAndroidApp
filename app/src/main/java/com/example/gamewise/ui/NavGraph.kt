package com.example.gamewise.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gamewise.ui.auth.LoginScreen
import com.example.gamewise.ui.auth.SignUpScreen
import com.example.gamewise.ui.screens.*

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Home : Screen("home")
    object Game : Screen("game")
    object Ideas : Screen("ideas")
    object Assessment : Screen("assessment")
    object TimeSpent : Screen("time_spent")
    object AiAssistant : Screen("ai_assistant")
    object Support : Screen("support")
    object Settings : Screen("settings")
    object Profile : Screen("profile")
}

@Composable
fun GameWiseNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Login.route) { inclusive = true } } },
                onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) }
            )
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUpSuccess = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Login.route) { inclusive = true } } },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable(Screen.Home.route) { HomeScreen() }
        composable(Screen.Game.route) { GameScreen() }
        composable(Screen.Ideas.route) { IdeasScreen() }
        composable(Screen.Assessment.route) { AssessmentScreen() }
        composable(Screen.TimeSpent.route) { TimeSpentScreen() }
        composable(Screen.AiAssistant.route) { AiAssistantScreen() }
        composable(Screen.Support.route) { SupportScreen() }
        composable(Screen.Settings.route) { 
            SettingsScreen(onNavigateToProfile = { navController.navigate(Screen.Profile.route) }) 
        }
        composable(Screen.Profile.route) { 
            ProfileScreen(
                onSignOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
