package com.example.focusonplus

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext

@Composable
fun OnboardingScreen(navController: NavController) {

    val context = LocalContext.current
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    // Check if user setup is done
    val userName = prefs.getString("user_name", null)

    // If already set → skip onboarding directly
    LaunchedEffect(Unit) {
        if (false) {
            navController.navigate("home") {
                popUpTo("onboarding") { inclusive = true }
            }
        }
    }

    // ONBOARDING UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE5E8F9)), // Outside background (light lavender)
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .fillMaxHeight(0.85f)
                .background(Color(0xFFB8C0EA), RoundedCornerShape(26.dp)) // Outer container
                .padding(22.dp),
            contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(Color(0xFFDCE2FB), RoundedCornerShape(25.dp)) // Inner light container
                    .padding(28.dp),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxSize()
                ) {

                    Spacer(modifier = Modifier.height(10.dp))

                    // IMAGE
                    Image(
                        painter = painterResource(id = R.drawable.onboarding_illustration),
                        contentDescription = "Onboarding Image",
                        modifier = Modifier.size(230.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // TITLE
                    Text(
                        text = "FocusOn+",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1C2340)
                    )

                    Text(
                        text = "Your mindful assistant for focused learning",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // BUTTON
                    Button(
                        onClick = {
                            navController.navigate("account_setup") {
                                popUpTo("onboarding") { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .height(55.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF324A9B) // Your dark blue primary
                        )
                    ) {
                        Text(
                            "Let’s Start !",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
