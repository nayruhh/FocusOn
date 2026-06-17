package com.example.focusonplus

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

@Composable

fun TimerCameraScreen(navController: NavController, duration: String) {
    val context = LocalContext.current
    // Convert "HH:MM:SS" → total seconds
    fun parseToSeconds(text: String): Int {
        val parts = text.split(":")
        val h = parts[0].toInt()
        val m = parts[1].toInt()
        val s = parts[2].toInt()
        return h * 3600 + m * 60 + s
    }

    // Convert seconds back to "HH:MM:SS"
    fun formatTime(seconds: Int): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return String.format("%02d:%02d:%02d", h, m, s)
    }

    var totalSeconds by remember { mutableStateOf(parseToSeconds(duration)) }
    val startingSeconds = parseToSeconds(duration)
    var isRunning by remember { mutableStateOf(true) }

    // Will be replace with face detection logic
    var faceDetected by remember { mutableStateOf(true) }

    // Countdown Effect
    LaunchedEffect(isRunning) {
        while (isRunning && totalSeconds > 0) {
            delay(1000L)
            totalSeconds -= 1
        }


        if (totalSeconds <= 0) {

            val minutes = startingSeconds / 60
            val distractions = 0 // update when real detection is added
            val accuracy = 100  // placeholder for now

            val calendar = Calendar.getInstance()
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
            val mappedDay = if (dayOfWeek == 0) 7 else dayOfWeek  // convert Sunday

            AnalyticsStorage.saveSession(
                context = context,
                record = SessionRecord(minutes, distractions, accuracy, mappedDay)
            )

            navController.navigate("session_summary/$minutes/$distractions") {
                popUpTo("timer_camera/{duration}") { inclusive = true }
            }
        }

    }

    val timeText = formatTime(totalSeconds)

    // ---------------- UI ----------------
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F5FC))
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        // Title
        Text(
            text = "Focus Timer",
            fontSize = 28.sp,
            color = Color(0xFF1C2340),
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )

        // Timer Box
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(25.dp))
                .background(Color.White)
                .padding(vertical = 20.dp)
        ) {
            Text(
                text = timeText,
                fontSize = 36.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = Color(0xFF1C2340),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Face Detected Indicator
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(if (faceDetected) Color(0xFFB7F7C5) else Color(0xFFFFC0C0))
                .padding(horizontal = 20.dp, vertical = 6.dp)
        ) {
            Text(
                text = if (faceDetected) "Face Detected" else "Face Not Detected",
                color = if (faceDetected) Color(0xFF1B7A27) else Color.Red,
                fontSize = 14.sp
            )
        }

        // Camera Preview Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(Color.LightGray)
        )

        // PAUSE/RESUME + STOP
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
        ) {

            // Toggle Pause / Resume
            Box(
                modifier = Modifier
                    .size(85.dp)
                    .clip(CircleShape)
                    .background(
                        if (isRunning) Color(0xFFDDE7B9) else Color(0xFFB0D7FF)
                    )
                    .clickable {
                        isRunning = !isRunning
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isRunning) "Pause" else "Resume",
                    fontSize = 18.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = if (isRunning) Color(0xFF5E6A21) else Color(0xFF0A3A8A)
                )
            }

            // Stop Button
            Box(
                modifier = Modifier
                    .size(85.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFD5D5))
                    .clickable {
                        totalSeconds = 0
                        navController.navigate("home")
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Stop",
                    fontSize = 18.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = Color(0xFF8A0000)
                )
            }
        }
    }
}
