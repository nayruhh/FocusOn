package com.example.focusonplus

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import androidx.core.content.edit

@Composable
fun AccountSettingsScreen(navController: NavController, context: Context) {

    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    var name by remember { mutableStateOf(prefs.getString("user_name", "") ?: "") }
    var category by remember { mutableStateOf(prefs.getString("user_category", "") ?: "") }
    var profileUri by remember { mutableStateOf(prefs.getString("user_image", "") ?: "") }

    // Image Picker
    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            profileUri = it.toString()
            prefs.edit { putString("user_image", profileUri) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F5FC))
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // ========================= TOP HEADER ===============================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, start = 20.dp, end = 20.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .padding(25.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0E4F4))
                        .clickable { picker.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (profileUri.isNotEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(Uri.parse(profileUri)),
                            contentDescription = "Profile",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.addprofile),
                            contentDescription = "Default",
                            tint = Color(0xFF3D4979),
                            modifier = Modifier.size(70.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C2340)
                )
                Text(
                    text = category,
                    fontSize = 15.sp,
                    color = Color(0xFF6B7280)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ========================= MAIN CONTENT ===============================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .weight(1f),
            ) {

                // -------- PROFILE CARD ----------
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .padding(25.dp)
                ) {

                    Text("Profile", fontSize = 17.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(20.dp))

                    // Name
                    Text("Name", fontSize = 14.sp)
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(25.dp))

                    // Category
                    Text("Change Your Category", fontSize = 14.sp)

                    val categories = listOf("University Student", "High School Student", "Working Adult")

                    Spacer(modifier = Modifier.height(10.dp))

                    Column {
                        categories.chunked(2).forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                row.forEach { item ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(45.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (category == item)
                                                    Color(0xFF636C90)
                                                else
                                                    Color.White
                                            )
                                            .border(1.dp, Color(0xFF636C90), RoundedCornerShape(12.dp))
                                            .clickable { category = item },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = item,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if (category == item) Color.White else Color(0xFF3D4979)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // ---- Save Changes ----
                    Button(
                        onClick = {
                            prefs.edit {
                                putString("user_name", name)
                                putString("user_category", category)
                                putString("user_image", profileUri)
                            }
                            Toast.makeText(context, "Changes saved", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFF3D4979))
                    ) {
                        Text("Save Changes", color = Color.White, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // -------- RESET SECTION ----------
                Text(
                    text = "Reset Your Focus History",
                    fontSize = 14.sp,
                    color = Color(0xFF1C2340),
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )

                Button(
                    onClick = {
                        prefs.edit { putInt("focus_minutes", 0) }
                        Toast.makeText(context, "Focus history reset", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFF3D4979)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Reset", color = Color.White)
                }
            }

            // ========================= FIXED BOTTOM NAV ===============================
            SettingsBottomNavBar(
                navController = navController,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
            )
        }
    }
}

// ======================== BOTTOM NAV BAR ===============================
@Composable
fun SettingsBottomNavBar(navController: NavController, modifier: Modifier = Modifier) {
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
                iconRes = R.drawable.homepage,
                label = "Home",
                onClick = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )

            BottomNavItem(
                iconRes = R.drawable.badges,
                label = "Badges",
                onClick = { navController.navigate("badges") }
            )
        }
    }
}
