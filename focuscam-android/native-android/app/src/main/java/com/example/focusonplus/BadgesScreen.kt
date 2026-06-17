package com.example.focusonplus

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import android.content.Context

@Composable
fun BadgesScreen(navController: NavController) {

    val context = LocalContext.current
    val prefs = context.getSharedPreferences("analytics", Context.MODE_PRIVATE)

    // Example dynamic values (you can update these later with real data)
    val streak = prefs.getInt("current_streak", 0)
    val sessionsToNextBadge = 2
    val progressPercent = 78 // example

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F5FC))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Your Focus Journey",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1C2340)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // BADGES GRID
        BadgesGrid()

        Spacer(modifier = Modifier.height(25.dp))

        // PROGRESS BAR
        ProgressBar(progressPercent)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Keep it up! $sessionsToNextBadge more sessions to next badge!",
            fontSize = 14.sp,
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Current Streak
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.fire_badge),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Current Streak: $streak days",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1C2340)
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        // Motivational Box
        MotivationalBox()

        Spacer(modifier = Modifier.height(20.dp))

        // NAV BUTTONS

        Button(
            onClick = { navController.navigate("home") },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(Color.White)
        ) {
            Text("Back To Home", color = Color(0xFF324A9B), fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { navController.navigate("analytics") },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFF324A9B))
        ) {
            Text("View Analytics", color = Color.White, fontSize = 16.sp)
        }
    }
}

@Composable
fun BadgesGrid() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BadgeItem(R.drawable.badge_7day, "7 Days")
            BadgeItem(R.drawable.badge_10hours, "10 Hours")
            BadgeItem(R.drawable.badge_locked, "Locked")
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BadgeItem(R.drawable.badge_locked, "Locked")
            BadgeItem(R.drawable.badge_locked, "Locked")
            BadgeItem(R.drawable.badge_21day, "21 Days")
        }
    }
}

@Composable
fun BadgeItem(iconRes: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(85.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(text = label, fontSize = 12.sp, color = Color(0xFF6B7280))
    }
}

@Composable
fun ProgressBar(percent: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(22.dp)
            .background(Color(0xFFDDE3F7), RoundedCornerShape(30.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(percent / 100f)
                .fillMaxHeight()
                .background(Color(0xFF324A9B), RoundedCornerShape(30.dp))
        )

        Text(
            text = "$percent %",
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun MotivationalBox() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFDDE3F7), RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "You're doing amazing!",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1C2340)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Focus for 30 more minutes to unlock your next badge",
            fontSize = 14.sp,
            color = Color(0xFF324A9B),
            modifier = Modifier.padding(horizontal = 10.dp),
        )
    }
}
