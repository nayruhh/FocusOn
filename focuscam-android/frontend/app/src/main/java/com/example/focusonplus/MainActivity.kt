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
        composable("analytics") {
            AnalyticsScreen(navController)
        }

        // ACCOUNT SETTINGS SCREEN
        composable("account_settings") {
            AccountSettingsScreen(
                navController,
                context = navController.context
            )
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
