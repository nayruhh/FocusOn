package com.example.focusonplus

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(navController: NavController) {

    val context = LocalContext.current
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val savedName = prefs.getString("user_name", "User") ?: "User"
    val streak = prefs.getInt("current_streak", 0)

    var timerText by remember { mutableStateOf("00:00:00") }
    var selectedDuration by remember { mutableStateOf<String?>(null) }

    var showCustomPicker by remember { mutableStateOf(false) }
    var selectedHour by remember { mutableIntStateOf(0) }
    var selectedMinute by remember { mutableIntStateOf(0) }
    var selectedSecond by remember { mutableIntStateOf(0) }

    val hours = (0..5).toList()
    val minutes = (0..59).toList()
    val seconds = (0..59).toList()

    // Root container
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F5FC))
    ) {

        // --------- MAIN CONTENT ----------
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .padding(bottom = 65.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // TOP SECTION (18%)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.18f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "Hi, $savedName",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C2340)
                )

                Text(
                    text = "Ready to focus today?",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.streak),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("$streak-Day Streak", fontSize = 14.sp)
                }
            }

            // MIDDLE SECTION (60%)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.60f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {

                Box(
                    modifier = Modifier
                        .size(285.dp)
                        .border(6.dp, Color(0xFFB9C3E6), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = timerText,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1C2340)
                        )
                        Text("Select Focus Duration", fontSize = 13.sp, color = Color.Gray)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("15 mins", "30 mins", "60 mins", "Custom").forEach { label ->
                        DurationButton(
                            label = label,
                            selected = selectedDuration == label,
                            onClick = {
                                selectedDuration = label
                                when (label) {
                                    "15 mins" -> timerText = "00:15:00"
                                    "30 mins" -> timerText = "00:30:00"
                                    "60 mins" -> timerText = "01:00:00"
                                    "Custom" -> showCustomPicker = true
                                }
                            }
                        )
                    }
                }

                Button(
                    onClick = {
                        if (timerText != "00:00:00") {
                            navController.navigate("timer_camera/$timerText")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFF324A9B)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Start Focus Session", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // --------- BOTTOM NAV ----------
        HomeBottomNavBar(
            navController = navController,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
    }

    if (showCustomPicker) {
        CustomTimePicker(
            onConfirm = {
                // Prevent 00:00:00 sessions
                if (!(selectedHour == 0 && selectedMinute == 0 && selectedSecond == 0)) {
                    timerText = String.format(
                        "%02d:%02d:%02d",
                        selectedHour,
                        selectedMinute,
                        selectedSecond
                    )
                    showCustomPicker = false
                }
            },
            onCancel = { showCustomPicker = false },
            hours = hours,
            minutes = minutes,
            seconds = seconds,
            onHourSelected = { selectedHour = it },
            onMinuteSelected = { selectedMinute = it },
            onSecondSelected = { selectedSecond = it }
        )
    }
}

@Composable
fun DurationButton(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(80.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) Color(0xFF324A9B) else Color.White)
            .border(2.dp, Color(0xFF324A9B), RoundedCornerShape(10.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            color = if (selected) Color.White else Color(0xFF324A9B),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun HomeBottomNavBar(navController: NavController, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color.White)
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            BottomNavItem(
                iconRes = R.drawable.analytics,
                label = "Analytics",
                onClick = { navController.navigate("analytics") }
            )

            BottomNavItem(
                iconRes = R.drawable.badges,
                label = "Badges",
                onClick = { navController.navigate("badges") }
            )

            BottomNavItem(
                iconRes = R.drawable.account,
                label = "Account",
                onClick = { navController.navigate("account_settings") }
            )
        }
    }
}

@Composable
fun BottomNavItem(iconRes: Int, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(28.dp),
            tint = Color.Unspecified
        )
        Text(label, fontSize = 11.sp, color = Color(0xFF324A9B))
    }
}

@Composable
fun CustomTimePicker(
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    hours: List<Int>,
    minutes: List<Int>,
    seconds: List<Int>,
    onHourSelected: (Int) -> Unit,
    onMinuteSelected: (Int) -> Unit,
    onSecondSelected: (Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onCancel() },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("OK")
            }
        },
        dismissButton = { TextButton(onClick = onCancel) { Text("Cancel") } },
        title = { Text("Select Custom Duration") },
        text = {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                TimePickerColumn("Hrs", hours, onHourSelected)
                TimePickerColumn("Min", minutes, onMinuteSelected)
                TimePickerColumn("Sec", seconds, onSecondSelected)
            }
        }
    )
}

@Composable
fun TimePickerColumn(label: String, items: List<Int>, onSelect: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontWeight = FontWeight.Bold)
        LazyColumn(modifier = Modifier.height(120.dp)) {
            items(items) { value ->
                Text(
                    text = value.toString().padStart(2, '0'),
                    fontSize = 18.sp,
                    color = Color(0xFF324A9B),
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { onSelect(value) }
                )
            }
        }
    }
}

fun updateUserStreak(context: Context) {
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val editor = prefs.edit()

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = dateFormat.format(Date())

    val lastDate = prefs.getString("last_focus_date", null)
    val currentStreak = prefs.getInt("current_streak", 0)

    val newStreak = if (lastDate == null) {
        1
    } else {
        val cal = Calendar.getInstance()
        cal.time = Date()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = dateFormat.format(cal.time)

        when (lastDate) {
            today -> currentStreak
            yesterday -> currentStreak + 1
            else -> 1
        }
    }

    editor.putInt("current_streak", newStreak)
    editor.putString("last_focus_date", today)
    editor.apply()
}
