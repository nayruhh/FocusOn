package com.example.focusonplus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.focusonplus.ui.theme.FocusOnPlusTheme
import android.content.Context
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FocusOnPlusTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "onboarding"
    ) {
        composable("onboarding") {
            OnboardingScreen(navController)
        }
        // ACCOUNT SETUP SCREEN
        composable("account_setup") {
            AccountSetupScreen(navController)
        }

        // HOME SCREEN
        composable("home") {
            HomeScreen(navController)
        }

        // TIMER CAMERA SCREEN
        composable("timer_camera/{duration}") { backStackEntry ->
            val duration = backStackEntry.arguments?.getString("duration") ?: "00:00:00"
            TimerCameraScreen(navController, duration)
        }
        composable("session_summary/{minutes}/{distractions}") { backStackEntry ->
            val minutes = backStackEntry.arguments?.getString("minutes")?.toInt() ?: 0
            val distractions = backStackEntry.arguments?.getString("distractions")?.toInt() ?: 0

            val context = LocalContext.current
            val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val name = prefs.getString("user_name", "User") ?: "User"

            SessionSummaryScreen(
                navController = navController,
                userName = name,
                totalMinutes = minutes,
                distractions = distractions
            )
        }
        // ANALYTICS SCREEN
        composable("analytics") {
            AnalyticsScreen(navController)
        }
        composable("badges") {
            BadgesScreen(navController)
        }


        // ACCOUNT SETTINGS SCREEN
        composable("account_settings") {
            val context = LocalContext.current
            AccountSettingsScreen(navController, context)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewMain() {
    FocusOnPlusTheme {
        AppNavigation()
    }
}
