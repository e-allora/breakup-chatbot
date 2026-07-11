package com.eallora.breakupchatbot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eallora.breakupchatbot.chat.ChatScreen
import com.eallora.breakupchatbot.onboarding.PersonaSelectionScreen
import com.eallora.breakupchatbot.settings.ApiKeySetupScreen
import com.eallora.breakupchatbot.settings.PrivacyScreen
import com.eallora.breakupchatbot.ui.theme.ChatbotTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatbotTheme {
                AppNavHost()
            }
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "chat"
    ) {
        composable("chat") {
            ChatScreen(
                onNavigateToExercises = { /* TODO: exercises */ },
                onNavigateToAnalytics = { /* TODO: analytics */ },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        composable("onboarding/persona_selection") {
            PersonaSelectionScreen(
                source = "onboarding",
                onPersonaSelected = { navController.popBackStack() },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("settings") {
            PrivacyScreen(
                onNavigateToApiKeySetup = { navController.navigate("settings/api_key") },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("settings/api_key") {
            ApiKeySetupScreen(
                source = "settings",
                onSaved = { navController.popBackStack() },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}