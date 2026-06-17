package com.example.focusonplus

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.mlkit.vision.face.FaceDetector
import kotlinx.coroutines.delay
import java.util.Calendar

@OptIn(ExperimentalGetImage::class)
@Composable
fun TimerCameraScreen(
    navController: NavController,
    duration: String
) {
    val context = LocalContext.current

    var faceDetected by remember { mutableStateOf(true) }
    var seconds by remember { mutableStateOf(0) }
    var studying by remember { mutableStateOf(false) }
    var paused by remember { mutableStateOf(false) }
    var grace by remember { mutableStateOf(0) }
    var totalDistractions by remember { mutableStateOf(0) }
    var hasDetectedFaceOnce by remember { mutableStateOf(false) }
    var warmUpSeconds by remember { mutableStateOf(0) }

    // Check camera permission
    val hasCameraPermission = remember {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    var permissionGranted by remember { mutableStateOf(hasCameraPermission) }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        permissionGranted = isGranted
    }

    // Request permission if not granted
    LaunchedEffect(Unit) {
        if (!permissionGranted) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val detector: FaceDetector = remember {
        MLKitFaceDetector.provideDetector()
    }

    // Timer logic - properly incrementing with coroutines (only when studying and not paused)
    LaunchedEffect(studying, paused) {
        while (studying && !paused) {
            delay(1000)
            if (studying && !paused) {
                seconds++
                if (warmUpSeconds < 15) {
                    warmUpSeconds++
                }
            }
        }
    }

    // Reset when study session starts
    LaunchedEffect(studying) {
        if (studying) {
            warmUpSeconds = 0
            hasDetectedFaceOnce = false
            grace = 0
            paused = false
        }
    }

    // Beautiful UI with proper styling
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F5FC))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Camera Preview Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (permissionGranted) {
                        CameraPreview(
                            context = context,
                            detector = detector,
                            onFaceDetected = { found ->
                                faceDetected = found
                                
                                if (found) {
                                    hasDetectedFaceOnce = true
                                    grace = 0
                                    // Resume session if it was paused
                                    if (paused && studying) {
                                        paused = false
                                    }
                                } else if (studying && !paused) {
                                    // Only count grace period if warm-up is complete and we've detected a face
                                    if (warmUpSeconds >= 15 && hasDetectedFaceOnce) {
                                        grace++
                                        // Pause session after 5 seconds of no face detection
                                        if (grace >= 5) {
                                            paused = true
                                            totalDistractions++
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                        
                        // Face detection status overlay
                        if (studying) {
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(16.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = when {
                                    paused -> Color(0xFFFF9800).copy(alpha = 0.9f) // Orange for paused
                                    faceDetected -> Color(0xFF4CAF50).copy(alpha = 0.9f) // Green for detected
                                    else -> Color(0xFFF44336).copy(alpha = 0.9f) // Red for not detected
                                }
                            ) {
                                Text(
                                    text = when {
                                        paused -> "⏸ Session Paused - Return to Resume"
                                        faceDetected -> "✓ Face Detected"
                                        warmUpSeconds < 15 -> "Initializing..."
                                        else -> "⚠ No Face Detected (${maxOf(0, 5 - grace)}s)"
                                    },
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Camera permission required",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        permissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                ) {
                                    Text("Grant Permission")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Timer Display Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = formatTime(seconds),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (paused) Color(0xFFFF9800) else Color(0xFF1C2340)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = when {
                            paused -> "Session Paused"
                            studying -> "Focus Session Active"
                            else -> "Ready to Start"
                        },
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Control Button
            Button(
                onClick = {
                    if (studying) {
                        studying = false
                        saveAndNavigate(context, seconds, totalDistractions, navController)
                    } else {
                        seconds = 0
                        grace = 0
                        totalDistractions = 0
                        warmUpSeconds = 0
                        hasDetectedFaceOnce = false
                        paused = false
                        studying = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (studying) Color(0xFFF44336) else Color(0xFF324A9B)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (studying) "Stop Session" else "Start Focus Session",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun formatTime(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, secs)
    } else {
        String.format("%02d:%02d", minutes, secs)
    }
}

fun saveAndNavigate(
    context: android.content.Context,
    seconds: Int,
    distractions: Int,
    navController: NavController
) {
    val totalMinutes = seconds / 60
    
    val accuracy = when {
        distractions == 0 -> 100
        else -> maxOf(0, 100 - distractions * 5)
    }
    
    val calendar = Calendar.getInstance()
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val adjustedDay = if (dayOfWeek == Calendar.SUNDAY) 7 else dayOfWeek - 1
    
    if (totalMinutes > 0) {
        AnalyticsStorage.saveSession(
            context,
            SessionRecord(
                minutes = totalMinutes,
                distractions = distractions,
                accuracy = accuracy,
                dayOfWeek = adjustedDay
            )
        )
    }
    
    navController.navigate("session_summary/$totalMinutes/$distractions")
}
