package com.example.focusonplus

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.math.roundToInt

@Composable
fun AnalyticsScreen(navController: NavController) {

    val context = LocalContext.current
    val records = AnalyticsStorage.getSessionHistory(context)

    // Total minutes
    val totalMinutes = records.sumOf { it.minutes }
    val totalHours = totalMinutes / 60f

    // Average session
    val avgSession = if (records.isNotEmpty()) {
        records.map { it.minutes }.average().roundToInt()
    } else 0

    // Best focus day (1–7)
    val dayTotals = IntArray(7)
    records.forEach { dayTotals[it.dayOfWeek - 1] += it.minutes }

    val bestDayIndex = dayTotals.indices.maxByOrNull { dayTotals[it] } ?: 0
    val bestDayName = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")[bestDayIndex]

    // Average accuracy
    val avgAccuracy = if (records.isNotEmpty()) {
        records.map { it.accuracy }.average().roundToInt()
    } else 0

    // Distractions per hour
    val distractionsPerHour =
        if (totalHours > 0) {
            records.sumOf { it.distractions } / totalHours
        } else 0.0

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F5FC)),
        contentAlignment = Alignment.TopCenter
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                "Analytics & Insights",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1C2340),
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // WEEKLY BAR CHART
            WeeklyBarChart(dayTotals.toList())

            Spacer(modifier = Modifier.height(20.dp))

            // STATS
            Text("Total Hours: ${"%.1f".format(totalHours)} h")
            Text("Best Focus Day: $bestDayName")
            Text("Average Session: $avgSession mins")

            Spacer(modifier = Modifier.height(20.dp))

            // ACCURACY CARD
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Focus Accuracy", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C2340))
                    Text("$avgAccuracy %", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF324A9B))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // DISTRACTIONS + STREAK ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                StatCard("Distractions", "${"%.1f".format(distractionsPerHour)} / hr")
                StatCard("Current\nStreak", "${getStreak(context)} days")
            }

            Spacer(modifier = Modifier.height(26.dp))

            // BUTTONS
            Button(
                onClick = { navController.navigate("badges") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFF324A9B)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("View Badges", color = Color.White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { navController.navigate("home") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(Color.White),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Back To Home", color = Color(0xFF324A9B), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String) {
    Box(
        modifier = Modifier
            .width(160.dp)
            .background(Color.White, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Text(title, fontSize = 14.sp, color = Color(0xFF6B7280))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C2340))
        }
    }
}

fun getStreak(context: Context): Int {
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    return prefs.getInt("current_streak", 0)
}
