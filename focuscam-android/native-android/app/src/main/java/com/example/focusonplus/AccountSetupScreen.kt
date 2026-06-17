package com.example.focusonplus

import android.content.Context
import android.net.Uri
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

@Composable
fun AccountSetupScreen(navController: NavController) {

    var name by remember { mutableStateOf("") }
    val categories = listOf("University Student", "High School Student", "Working Adult")
    var selectedCategory by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var errorMessage by remember { mutableStateOf("") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
        errorMessage = ""
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(23.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight()
                .background(Color(0xFFE1E5F5), RoundedCornerShape(20.dp))
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // TOP TEXT (15% HEIGHT)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.15f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Welcome to FocusOn+",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1C2340)
                )
                Text(
                    text = "Set Up Your Profile",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C2340)
                )
            }

            // PROFILE PICTURE (25%)
            Column(
                modifier = Modifier.weight(0.25f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD9DFF2))
                        .clickable { imagePickerLauncher.launch("image/*") }
                        .border(2.dp, Color(0xFF3D4979), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(selectedImageUri),
                            contentDescription = "Profile Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.addprofile),
                            contentDescription = "Default Profile",
                            modifier = Modifier.size(70.dp)
                        )
                    }
                }
            }

            // INPUT FIELDS (40%)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.40f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                // Name Label
                Text(
                    text = "Name",
                    fontSize = 14.sp,
                    color = Color(0xFF3D4979),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Start)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        errorMessage = ""
                    },
                    placeholder = { Text("Enter your name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Select Your Category",
                    fontSize = 14.sp,
                    color = Color(0xFF1C2340),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(6.dp))

                categories.forEach { category ->
                    val isSelected = selectedCategory == category

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Color(0xFF636C90) else Color.White)
                            .border(1.dp, Color(0xFF7981A1), RoundedCornerShape(10.dp))
                            .clickable {
                                selectedCategory = category
                                errorMessage = ""
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = category,
                            fontSize = 14.sp,
                            color = if (isSelected) Color.White else Color(0xFF3D4979),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(top = 6.dp)
                    )
                }
            }

            // BUTTON (20%)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.20f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                val isFormValid = name.isNotBlank() && selectedCategory.isNotBlank()

                Button(
                    onClick = {
                        if (!isFormValid) {
                            errorMessage = "Please fill in your name and select a category."
                        } else {
                            errorMessage = ""
                            saveUserData(
                                navController.context,
                                name,
                                selectedCategory,
                                selectedImageUri?.toString() ?: ""
                            )
                            navController.navigate("home") {
                                popUpTo("account_setup") { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFormValid) Color(0xFF3D4979) else Color(0xFF9FA8C3)
                    )
                ) {
                    Text(
                        text = "Save and Continue",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Your data stays on this device",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}

fun saveUserData(context: Context, name: String, category: String, profileUri: String) {
    val pref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    pref.edit()
        .putString("user_name", name)
        .putString("user_category", category)
        .putString("user_image", profileUri)
        .apply()
}
