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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SessionSummaryScreen(
    navController: NavController,
    userName: String,
    totalMinutes: Int,
    distractions: Int
) {

    // Calculate accuracy (simple formula)
    val focusAccuracy = when {
        distractions == 0 -> 100
        else -> maxOf(0, 100 - distractions * 5)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F5FC)),
        contentAlignment = Alignment.TopCenter
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // CONFETTI IMAGE
            Image(
                painter = painterResource(id = R.drawable.confetti_icon),
                contentDescription = "Confetti",
                modifier = Modifier.size(90.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // TITLE
            Text(
                text = "Session Completed!",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1C2340)
            )

            Spacer(modifier = Modifier.height(22.dp))

            // WHITE CARD
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(18.dp))
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                SessionRow("Total Focus Time", "$totalMinutes mins")
                DividerLine()
                SessionRow("Distractions", "$distractions times")
                DividerLine()
                SessionRow("Focus Accuracy", "$focusAccuracy%")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // MESSAGE
            Text(
                text = "Well done, $userName!",
                fontSize = 16.sp,
                color = Color(0xFF3D4979),
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Keep this streak going -\nSmall habits build big results!",
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                modifier = Modifier.padding(top = 6.dp),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(26.dp))

            // BUTTONS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PurpleSmallButton("View Analytics") {
                    navController.navigate("analytics")
                }
                PurpleSmallButton("View Badges") {
                    navController.navigate("badges")

                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BACK TO HOME
            Button(
                onClick = { navController.navigate("home") },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF324A9B)
                )
            ) {
                Text(
                    text = "Back To Home",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SessionRow(left: String, right: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(left, color = Color(0xFF1C2340), fontSize = 15.sp)
        Text(right, color = Color(0xFF1C2340), fontSize = 15.sp)
    }
}

@Composable
fun DividerLine() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFFE3E5F0))
    )
}

@Composable
fun PurpleSmallButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(150.dp)
            .height(45.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF324A9B),
            contentColor = Color.White
        )
    ) {
        Text(text)
    }
}
